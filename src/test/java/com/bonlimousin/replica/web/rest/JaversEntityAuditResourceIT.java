package com.bonlimousin.replica.web.rest;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.bonlimousin.replica.BonReplicaServiceApp;
import com.bonlimousin.replica.config.audit.EntityAuditAction;
import com.bonlimousin.replica.domain.BovineEntity;
import com.bonlimousin.replica.domain.enumeration.BovineStatus;
import com.bonlimousin.replica.domain.enumeration.Gender;
import com.bonlimousin.replica.domain.enumeration.HornStatus;
import com.bonlimousin.replica.repository.BovineRepository;
import com.bonlimousin.replica.security.AuthoritiesConstants;

@SpringBootTest(classes = BonReplicaServiceApp.class)
@AutoConfigureMockMvc
class JaversEntityAuditResourceIT {

	@Autowired
    private BovineRepository bovineRepository;
	
	@Autowired
	private Javers javers;
	
    @Autowired
    private MockMvc restMockMvc;
    
    public static BovineEntity createEntity() {
		BovineEntity bovineEntity = new BovineEntity()
				.earTagId(2000)
				.masterIdentifier("2000")
				.country("se")
				.herdId(15112)
				.birthDate(Instant.now())
				.gender(Gender.BULL)
				.name("bully")
				.bovineStatus(BovineStatus.ON_FARM)
				.hornStatus(HornStatus.HORNED)
				.matriId(20)
				.patriId(30)
				.weight0(43)
				.weight200(340)
				.weight365(630);
		return bovineEntity;
	}
    
    @WithAnonymousUser
    @Test    
    void testWithAnonymousUser() throws Exception {    	
        restMockMvc.perform(get("/api/audits/entity/all"))
            .andExpect(status().isUnauthorized());
    }
    
    @WithMockUser
    @Test    
    void testWithUser() throws Exception {    	
        restMockMvc.perform(get("/api/audits/entity/all"))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getAuditedEntities() throws Exception {    	
        restMockMvc.perform(get("/api/audits/entity/all"))
        	.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").value(hasItem("Blup")))
            .andExpect(jsonPath("$").value(hasItem("Bovine")))
            .andExpect(jsonPath("$").value(hasItem("JournalEntry")))
            .andExpect(jsonPath("$").value(hasItem("SourceFile")))
            ;
    }
    
    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getAllEntityChanges() throws Exception {
    	BovineEntity be = bovineRepository.saveAndFlush(createEntity());

        restMockMvc.perform(get("/api/audits/entity/changes")        		
        		.param("entityType", BovineEntity.class.getSimpleName())
        		.param("limit", "1")
        	)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[0].entityId").value(be.getId()))
            .andExpect(jsonPath("$.[0].entityType").value(be.getClass().getSimpleName()))
            .andExpect(jsonPath("$.[0].action").value(EntityAuditAction.CREATE.value()))
            ;        
    }
    
    @Test
    @Transactional
    @WithMockUser(authorities = AuthoritiesConstants.ADMIN)
    void getPrevVersion() throws Exception {
    	BovineEntity bePrev = bovineRepository.saveAndFlush(createEntity());
    	BovineEntity be = bovineRepository.saveAndFlush(bePrev.name("hoho"));

    	QueryBuilder jqlQuery = QueryBuilder.byClass(BovineEntity.class)
                .limit(1)
                .withNewObjectChanges(true);
    	CdoSnapshot snapshot =  javers.findSnapshots(jqlQuery.build()).get(0);
    	
        ResultActions ra = restMockMvc.perform(get("/api/audits/entity/changes/version/previous")
        	.param("qualifiedName", BovineEntity.class.getSimpleName())
        	.param("entityId", be.getId().toString())
        	.param("commitVersion", String.valueOf(snapshot.getVersion()))
        );
        ra.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.action").value(EntityAuditAction.CREATE.toString()))
            .andExpect(jsonPath("$.entityId").value(be.getId().toString()))
            .andExpect(jsonPath("$.entityType").value(BovineEntity.class.getName()))            
            ;
    }
    
}

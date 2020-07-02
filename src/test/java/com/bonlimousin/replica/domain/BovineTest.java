package com.bonlimousin.replica.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.replica.web.rest.TestUtil;

public class BovineTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BovineEntity.class);
        BovineEntity bovineEntity1 = new BovineEntity();
        bovineEntity1.setId(1L);
        BovineEntity bovineEntity2 = new BovineEntity();
        bovineEntity2.setId(bovineEntity1.getId());
        assertThat(bovineEntity1).isEqualTo(bovineEntity2);
        bovineEntity2.setId(2L);
        assertThat(bovineEntity1).isNotEqualTo(bovineEntity2);
        bovineEntity1.setId(null);
        assertThat(bovineEntity1).isNotEqualTo(bovineEntity2);
    }
}

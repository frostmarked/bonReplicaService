package com.bonlimousin.replica.service.entitychange;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.javers.core.commit.Commit;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Profile({ "dev", "emitentitychanges", "prod" })
@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE)
public class EntityChangeJaversAspect {

	private final EntityChangeService entityChangeService;

	public EntityChangeJaversAspect(EntityChangeService entityChangeService) {
		this.entityChangeService = entityChangeService;
	}

	@AfterReturning(pointcut = "execution(public * commit(..)) && this(org.javers.core.Javers)", returning = "commit")
	public void onJaversCommitExecuted(JoinPoint jp, Commit commit) {
		this.entityChangeService.broadcastEntityChange(commit);
	}
}
package com.bonlimousin.replica.config.audit;

import com.bonlimousin.replica.config.Constants;
import com.bonlimousin.replica.security.SecurityUtils;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

@Component
public class JaversAuthorProvider implements AuthorProvider {

   @Override
   public String provide() {
       return SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT);
   }
}

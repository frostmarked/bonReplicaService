package com.bonlimousin.replica.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.replica.web.rest.TestUtil;

public class BlupTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlupEntity.class);
        BlupEntity blupEntity1 = new BlupEntity();
        blupEntity1.setId(1L);
        BlupEntity blupEntity2 = new BlupEntity();
        blupEntity2.setId(blupEntity1.getId());
        assertThat(blupEntity1).isEqualTo(blupEntity2);
        blupEntity2.setId(2L);
        assertThat(blupEntity1).isNotEqualTo(blupEntity2);
        blupEntity1.setId(null);
        assertThat(blupEntity1).isNotEqualTo(blupEntity2);
    }
}

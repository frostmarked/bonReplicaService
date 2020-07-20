package com.bonlimousin.replica.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.replica.web.rest.TestUtil;

public class SourceFileTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SourceFileEntity.class);
        SourceFileEntity sourceFileEntity1 = new SourceFileEntity();
        sourceFileEntity1.setId(1L);
        SourceFileEntity sourceFileEntity2 = new SourceFileEntity();
        sourceFileEntity2.setId(sourceFileEntity1.getId());
        assertThat(sourceFileEntity1).isEqualTo(sourceFileEntity2);
        sourceFileEntity2.setId(2L);
        assertThat(sourceFileEntity1).isNotEqualTo(sourceFileEntity2);
        sourceFileEntity1.setId(null);
        assertThat(sourceFileEntity1).isNotEqualTo(sourceFileEntity2);
    }
}

package com.bonlimousin.replica.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.bonlimousin.replica.web.rest.TestUtil;

public class JournalEntryTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JournalEntryEntity.class);
        JournalEntryEntity journalEntryEntity1 = new JournalEntryEntity();
        journalEntryEntity1.setId(1L);
        JournalEntryEntity journalEntryEntity2 = new JournalEntryEntity();
        journalEntryEntity2.setId(journalEntryEntity1.getId());
        assertThat(journalEntryEntity1).isEqualTo(journalEntryEntity2);
        journalEntryEntity2.setId(2L);
        assertThat(journalEntryEntity1).isNotEqualTo(journalEntryEntity2);
        journalEntryEntity1.setId(null);
        assertThat(journalEntryEntity1).isNotEqualTo(journalEntryEntity2);
    }
}

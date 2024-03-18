package fi.vm.sade.valinta.seuranta.dokumentti.repository;

import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

public interface DokumenttiRepository {
    DokumenttiDto get(final String key);
    String save(final String documentId,
                final String fileName,
                final Collection<String> tags,
                final String contentType,
                final DokumenttiDto dokumentti);
    DokumenttiDto update(final String key, final DokumenttiDto dokumentti);
    void delete(final String key);
}

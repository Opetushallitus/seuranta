package fi.vm.sade.valinta.seuranta.dokumentti;

import fi.vm.sade.valinta.seuranta.dokumentti.repository.DokumenttiRepository;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class DokumenttiRepositoryMock implements DokumenttiRepository {
    private volatile Map<String, DokumenttiDto> storage = new HashMap<>();
    @Override
    public DokumenttiDto get(final String key) {
        if (!storage.containsKey(key)) {
            throw new CompletionException(NoSuchKeyException.builder().build());
        }
        return storage.get(key);
    }

    @Override
    public String save(final String documentId,
                       final String fileName,
                       final Date expirationDate,
                       final Collection<String> tags,
                       final String contentType,
                       final DokumenttiDto dokumentti) {
        final String key = tags.stream().map(t -> "t-" + t).collect(Collectors.joining("/")) + "/" + documentId;
        storage.put(key, dokumentti);
        return key;
    }

    @Override
    public DokumenttiDto update(final String key, final DokumenttiDto dokumentti) {
        storage.put(key, dokumentti);
        return dokumentti;
    }

    @Override
    public void delete(String key) {
        storage.remove(key);
    }
}

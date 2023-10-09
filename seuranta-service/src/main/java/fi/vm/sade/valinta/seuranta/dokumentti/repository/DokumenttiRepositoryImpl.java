package fi.vm.sade.valinta.seuranta.dokumentti.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.vm.sade.valinta.dokumenttipalvelu.Dokumenttipalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectEntity;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Repository
public class DokumenttiRepositoryImpl implements DokumenttiRepository {
    private static final Gson GSON = new Gson();
    private final Dokumenttipalvelu dokumenttipalvelu;

    @Autowired
    public DokumenttiRepositoryImpl(Dokumenttipalvelu dokumenttipalvelu) {
        this.dokumenttipalvelu = dokumenttipalvelu;
    }

    private String toJson(final DokumenttiDto dokumenttiDto) {
        return GSON.toJson(dokumenttiDto);
    }

    private DokumenttiDto fromJson(final String json) {
        return GSON.fromJson(json, new TypeToken<DokumenttiDto>() {}.getType());
    }

    @Override
    public DokumenttiDto get(final String key) {
        final ObjectEntity objectEntity = dokumenttipalvelu.get(key);
        return fromJson(new BufferedReader(
                new InputStreamReader(objectEntity.entity, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n")));
    }

    @Override
    public String save(final String documentId,
                       final String fileName,
                       final Date expirationDate,
                       final Collection<String> tags,
                       final String contentType,
                       final DokumenttiDto dokumentti) {
        final ObjectMetadata metadata = dokumenttipalvelu.save(
                documentId,
                fileName,
                expirationDate,
                tags,
                contentType,
                new ByteArrayInputStream(toJson(dokumentti).getBytes(StandardCharsets.UTF_8))
        );
        return metadata.key;
    }

    @Override
    public DokumenttiDto update(final String key, final DokumenttiDto updated) {
        final ObjectEntity objectEntity = dokumenttipalvelu.get(key);
        dokumenttipalvelu.save(
                objectEntity.documentId,
                objectEntity.fileName,
                Date.from(objectEntity.expires),
                objectEntity.tags,
                objectEntity.contentType,
                new ByteArrayInputStream(toJson(updated).getBytes(StandardCharsets.UTF_8))
        );
        return updated;
    }

    @Override
    public void delete(String key) {
        dokumenttipalvelu.delete(key);
    }
}

package fi.vm.sade.valinta.seuranta.dokumentti.repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.vm.sade.valinta.dokumenttipalvelu.Dokumenttipalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectEntity;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import fi.vm.sade.valinta.seuranta.resource.impl.DokumentinSeurantaResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

@Repository
public class DokumenttiRepositoryImpl implements DokumenttiRepository {
    private static final Gson GSON = new Gson();
    private final Dokumenttipalvelu dokumenttipalvelu;

    private static final Logger LOG = LoggerFactory.getLogger(DokumenttiRepositoryImpl.class);

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
    public DokumenttiDto get(final String id) {
        String key = dokumenttipalvelu.composeKey(Collections.singletonList("seuranta"),id);
        final ObjectEntity objectEntity = dokumenttipalvelu.get(key);

        return fromJson(new BufferedReader(
                new InputStreamReader(objectEntity.entity, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n")));
    }

    @Override
    public String save(final String documentId,
                       final String fileName,
                       final Collection<String> tags,
                       final String contentType,
                       final DokumenttiDto dokumentti) {
        LOG.info("Saving document {} by id {}", dokumentti, documentId);
        final ObjectMetadata metadata = dokumenttipalvelu.save(
                documentId,
                fileName,
                tags,
                contentType,
                new ByteArrayInputStream(toJson(dokumentti).getBytes(StandardCharsets.UTF_8))
        );
        return metadata.documentId;
    }

    @Override
    public DokumenttiDto update(final String id, final DokumenttiDto updated) {
        String key = dokumenttipalvelu.composeKey(Collections.singletonList("seuranta"), id);
        final ObjectEntity objectEntity = dokumenttipalvelu.get(key);
        LOG.info("Saving updated document {} by id {}", updated, id);
        dokumenttipalvelu.save(
                objectEntity.documentId,
                objectEntity.fileName,
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

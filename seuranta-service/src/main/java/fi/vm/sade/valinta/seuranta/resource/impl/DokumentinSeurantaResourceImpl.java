package fi.vm.sade.valinta.seuranta.resource.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.vm.sade.valinta.dokumenttipalvelu.Dokumenttipalvelu;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectEntity;
import fi.vm.sade.valinta.dokumenttipalvelu.dto.ObjectMetadata;
import fi.vm.sade.valinta.seuranta.dokumentti.repository.DokumenttiRepository;
import fi.vm.sade.valinta.seuranta.dto.DokumenttiDto;
import io.swagger.annotations.Api;
import fi.vm.sade.valinta.seuranta.dto.VirheilmoitusDto;

import static org.apache.commons.lang.StringUtils.*;

import fi.vm.sade.valinta.seuranta.resource.DokumentinSeurantaResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@PreAuthorize("isAuthenticated()")
@Component
@Path("dokumentinseuranta")
@Api(value = "/dokumentinseuranta", description = "Dokumentinseurantarajapinta")
public class DokumentinSeurantaResourceImpl implements DokumentinSeurantaResource {
    private static final Logger LOG = LoggerFactory.getLogger(DokumentinSeurantaResourceImpl.class);

    private final DokumenttiRepository dokumenttiRepository;

    @Autowired
    public DokumentinSeurantaResourceImpl(final DokumenttiRepository dokumenttiRepository) {
        this.dokumenttiRepository = dokumenttiRepository;
    }

    @Override
    public Response dokumentti(String key) {
        try {
            if (trimToNull(key) == null) {
                LOG.error("key({}) ei saa olla tyhjä!", key);
                throw new RuntimeException("Key ei saa olla tyhjä!");
            }
            return Response.ok(dokumenttiRepository.get(key)).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa dokumenttia luettauessa key=" + key, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response dokumentinTunniste(String key, String dokumenttiId) {
        try {
            if (trimToNull(dokumenttiId) == null || trimToNull(key) == null) {
                LOG.error("Key({}) tai dokumenttiId({}) ei saa olla tyhjä!", key, dokumenttiId);
                throw new RuntimeException("Key tai dokumenttiId ei saa olla tyhjä!");
            }
            final DokumenttiDto dokumentti = dokumenttiRepository.get(key);
            return Response.ok(dokumenttiRepository.update(key, new DokumenttiDto(
                    dokumentti.getUuid(),
                    dokumenttiId,
                    dokumentti.getKuvaus(),
                    dokumentti.getVirheilmoitukset()
            ))).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa dokumenttiId:tä(" + dokumenttiId + ") paivitettaessa key:lle " +  key, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response luoDokumentti(String kuvaus) {
        try {
            if (trimToNull(kuvaus) == null) {
                LOG.error("Kuvaus({}) ei saa olla tyhjä!", kuvaus);
                throw new RuntimeException("Kuvaus ei saa olla tyhjä!");
            }
            final DokumenttiDto dokumentti = new DokumenttiDto(
                    UUID.randomUUID().toString(),
                    null,
                    kuvaus,
                    null
            );
            final String id = dokumenttiRepository.save(
                    dokumentti.getUuid(),
                    "dokumentti_" + dokumentti.getUuid() + ".json",
                    Date.from(Instant.now().plus(30, ChronoUnit.DAYS)),  // TODO tarkista järkevä säilytysaika
                    Collections.singleton("seuranta"),
                    "application/json",
                    dokumentti);
            return Response.ok(id).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa uutta dokumenttia luotaessa kuvauksella " + kuvaus, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response paivitaKuvaus(String id, String kuvaus) {
        try {
            if (trimToNull(kuvaus) == null || trimToNull(id) == null) {
                LOG.error("Id({}) tai kuvaus({}) ei saa olla tyhjä!", id, kuvaus);
                throw new RuntimeException("Id tai kuvaus ei saa olla tyhjä!");
            }
            final DokumenttiDto dokumentti = dokumenttiRepository.get(id);
            return Response.ok(dokumenttiRepository.update(id, new DokumenttiDto(
                    dokumentti.getUuid(),
                    dokumentti.getDokumenttiId(),
                    kuvaus,
                    dokumentti.getVirheilmoitukset()
            ))).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa kuvausta " + kuvaus + " paivitettaessa id:lle " + id, t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response lisaaVirheita(String key, List<VirheilmoitusDto> virheita) {
        try {
            if (virheita == null || virheita.isEmpty() || trimToNull(key) == null) {
                LOG.error("Key({}) tai virheet({}) ei saa olla tyhjä!", key, virheita);
                throw new RuntimeException("Key tai virheet ei saa olla tyhjä!");
            }
            final DokumenttiDto dokumentti = dokumenttiRepository.get(key);
            return Response.ok(dokumenttiRepository.update(key, new DokumenttiDto(
                    dokumentti.getUuid(),
                    dokumentti.getDokumenttiId(),
                    dokumentti.getKuvaus(),
                    Stream.concat(dokumentti.getVirheilmoitukset() != null ?
                            dokumentti.getVirheilmoitukset().stream() : Stream.empty(),
                            virheita.stream()).collect(Collectors.toList())
            ))).build();
        } catch (Throwable t) {
            LOG.error("Poikkeus dokumentinseurannassa virhetiloja lisattaessa!", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }

    @Override
    public Response poista(String key) {
        try {
            if (trimToNull(key) == null) {
                LOG.error("Key({}) ei saa olla tyhjä!", key);
                throw new RuntimeException("Uuid ei saa olla tyhjä!");
            }
            dokumenttiRepository.delete(key);
            return Response.ok(key).build();
        } catch (Throwable t) {
            LOG.error("Poistaminen epäonnistui virheeseen!", t);
            return Response.serverError().entity(t.getMessage()).build();
        }
    }


}

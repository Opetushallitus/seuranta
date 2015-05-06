package fi.vm.sade.valinta.seuranta.laskenta.dao.impl;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import fi.vm.sade.valinta.seuranta.dto.HakukohdeDto;
import fi.vm.sade.valinta.seuranta.dto.LaskentaTyyppi;
import fi.vm.sade.valinta.seuranta.laskenta.dao.SeurantaDao;
import fi.vm.sade.valinta.seuranta.testcontext.SeurantaConfiguration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@ContextConfiguration(classes = SeurantaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SeurantaDaoLoadTest {
    @Rule public ConcurrentRule rule = new ConcurrentRule();

    @Autowired
    private SeurantaDao seurantaDao;

    private static final int NUMBER_OF_CONCURRENT_LASKENTAS = 100;
    private static Set<String> laskentaUuids = new HashSet<>();

    @PostConstruct
    public void createData() {
        IntStream.rangeClosed(1, NUMBER_OF_CONCURRENT_LASKENTAS).forEach(this::aloitaUusiLaskenta);
    }

    private String aloitaUusiLaskenta(int i) {
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));
        return seurantaDao.luoLaskenta("hk", LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids);
    }

    @Test
    @Concurrent(count = NUMBER_OF_CONCURRENT_LASKENTAS)
    public void testaaMonenSamanAikaisenLaskennanAloittaminenOnnistuu() {
        String laskentaUuid = seurantaDao.otaSeuraavaLaskentaTyonAlle();
        laskentaUuids.add(laskentaUuid);
        assertNotNull(laskentaUuid);
    }

    @AfterClass
    public static void assertUniqueLaskentaIds() {
        assertEquals(NUMBER_OF_CONCURRENT_LASKENTAS, laskentaUuids.size());
    }
}

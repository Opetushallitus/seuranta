package fi.vm.sade.valinta.seuranta.concurrency;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Works only with a real mongo and can not be run with flapdoodle.
 * This requires changing the MongoConfiguration to point at a real thing.
 * @see fi.vm.sade.valinta.seuranta.testcontext.MongoConfiguration#getMongo
 */
@ContextConfiguration(classes = SeurantaConfiguration.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSeurantaDaoConcurrentIntegration {
    @Rule public ConcurrentRule rule = new ConcurrentRule();

    @Autowired
    private SeurantaDao seurantaDao;

    private static final int NUMBER_OF_CONCURRENT_LASKENTAS = 500;
    private static Set<String> laskentaUuids = new HashSet<>();
    private static final AtomicInteger counter = new AtomicInteger();

    @PostConstruct
    public void createData() {
        IntStream.rangeClosed(1, NUMBER_OF_CONCURRENT_LASKENTAS).forEach(this::aloitaUusiLaskenta);
    }

    private String aloitaUusiLaskenta(int i) {
        Collection<HakukohdeDto> hakukohdeOids = Arrays.asList(new HakukohdeDto("h1", "o1"), new HakukohdeDto("h2", "o2"));
        return seurantaDao.luoLaskenta("U0","", "", "hk", LaskentaTyyppi.HAKU, true, null, null, hakukohdeOids);
    }

    @Ignore
    @Test
    @Concurrent(count = NUMBER_OF_CONCURRENT_LASKENTAS)
    public void testaaMonenSamanAikaisenLaskennanAloittaminenOnnistuu() {
        String laskentaUuid = seurantaDao.otaSeuraavaLaskentaTyonAlle();
        assertNotNull(laskentaUuid);
        laskentaUuids.add(laskentaUuid);
        counter.getAndIncrement();
    }

    @AfterClass
    public static void assertUniqueLaskentaIds() {
        if (testIgnored()) return;
        assertEquals(counter.get(), NUMBER_OF_CONCURRENT_LASKENTAS);
        assertEquals(NUMBER_OF_CONCURRENT_LASKENTAS, laskentaUuids.size());
    }

    private static boolean testIgnored() {
        return counter.get() == 0;
    }
}

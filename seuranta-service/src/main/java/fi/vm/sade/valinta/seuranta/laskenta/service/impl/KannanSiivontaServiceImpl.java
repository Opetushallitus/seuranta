package fi.vm.sade.valinta.seuranta.laskenta.service.impl;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fi.vm.sade.valinta.seuranta.laskenta.dao.SeurantaDao;

/**
 *         Siivoa paivaa vanhemmat laskennat pois kannasta. Tarkistus tehdaan
 *         kolmesti paivassa.
 */
@Component
public class KannanSiivontaServiceImpl extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(KannanSiivontaServiceImpl.class);
    private static final String NIMI = "Seurantakannan siivontadaemoni";
    private final long PERIOD = TimeUnit.HOURS.toMillis(8L);
    private final Timer timer;
    private final SeurantaDao seurantaDao;

    @Autowired
    public KannanSiivontaServiceImpl(SeurantaDao seurantaDao) {
        super();
        this.seurantaDao = seurantaDao;
        this.timer = new Timer(NIMI, true);
        this.timer.scheduleAtFixedRate(this, PERIOD, PERIOD);
    }

    @Override
    public void run() {
        LOG.info("Aloitetaan seurantakannan siivous");
        try {
            seurantaDao.siivoa(DateTime.now().minusDays(1).toDate());
        } catch (Exception e) {
            LOG.error("Seurantakannan siivous epaonnistui virheeseen {}",
                    e.getMessage());
        }
    }
}

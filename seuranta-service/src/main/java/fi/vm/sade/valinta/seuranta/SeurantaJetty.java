package fi.vm.sade.valinta.seuranta;

import fi.vm.sade.jetty.OpintopolkuJetty;

public class SeurantaJetty {
    public static void main(String[] args) {
        new OpintopolkuJetty().start("/seuranta-service");
    }
}

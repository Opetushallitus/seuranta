package fi.vm.sade.valinta.seuranta.dto;

public class TunnisteDto {
    private final String uuid;
    private final boolean luotiinkoUusiLaskenta;

    public TunnisteDto() {
        this.uuid = null;
        this.luotiinkoUusiLaskenta = true;
    }

    public TunnisteDto(String uuid, boolean luotiinkoUusiLaskenta) {
        this.uuid = uuid;
        this.luotiinkoUusiLaskenta = luotiinkoUusiLaskenta;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean getLuotiinkoUusiLaskenta() {
        return luotiinkoUusiLaskenta;
    }

    public boolean isLuotiinkoUusiLaskenta() {
        return luotiinkoUusiLaskenta;
    }
}

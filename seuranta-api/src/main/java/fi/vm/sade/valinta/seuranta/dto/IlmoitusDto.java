package fi.vm.sade.valinta.seuranta.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
public class IlmoitusDto {

	private IlmoitusTyyppi tyyppi;
	private String otsikko;
	private Date paivamaara;
	private List<String> data;

	public IlmoitusDto(IlmoitusTyyppi tyyppi, String otsikko, List<String> data) {
		this.tyyppi = tyyppi;
		this.otsikko = otsikko;
		this.data = data;
		this.paivamaara = new Date();
	}

	public IlmoitusDto(IlmoitusTyyppi tyyppi, String otsikko,
			List<String> data, Date paivamaara) {
		this.tyyppi = tyyppi;
		this.otsikko = otsikko;
		this.data = data;
		this.paivamaara = paivamaara;
	}

	public Date getPaivamaara() {
		return paivamaara;
	}

	public List<String> getData() {
		return data;
	}

	public String getOtsikko() {
		return otsikko;
	}

	public IlmoitusTyyppi getTyyppi() {
		return tyyppi;
	}

	public static IlmoitusDto virheilmoitus(String virhe, String... dataa) {
		return new IlmoitusDto(IlmoitusTyyppi.VIRHE, virhe,
				Arrays.asList(dataa));
	}
}

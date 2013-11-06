package schedule.mock.utils;

import org.apache.commons.lang3.ArrayUtils;

import schedule.mock.data.DOAddressComponent;
import schedule.mock.data.DOGeocodeResult;


public final class GeocodeUtil {

	public static GeocodedAddress fromGeocodeResult(DOGeocodeResult _geocodeResult) {
		GeocodedAddress address = null;
		DOAddressComponent[] components = _geocodeResult.getDOAddressComponents();
		if (components != null && components.length > 0) {
			address = new GeocodedAddress();
			boolean hadSublocality = false;// The property might be duplicated.
			boolean hadLocality = false;// The property might be duplicated.
			for (DOAddressComponent c : components) {
				// To find type i.e "route" for street, "street_number" etc...
				String[] types = c.getTypes();
				if (types != null && types.length > 0) {
					if (ArrayUtils.contains(types, "street_number")) {
						address.Number.append(c.getShortName());
						address.Street.append(' ');
					} else if (ArrayUtils.contains(types, "route")) {
						address.Street.append(c.getShortName());
						address.Street.append(' ');
					} else if (ArrayUtils.contains(types, "sublocality") && !hadSublocality) {
						address.City.append(c.getShortName());
						address.City.append(' ');
						hadSublocality = true;
					} else if (ArrayUtils.contains(types, "locality") && !hadLocality) {
						address.City.append(c.getShortName());
						address.City.append(' ');
						hadLocality = true;
					} else if (ArrayUtils.contains(types, "country")) {
						address.Country.append(c.getLongName());
						address.Country.append(' ');
					}
				}
			}
		}
		return address;
	}


	public static class GeocodedAddress {

		StringBuilder Street = new StringBuilder();
		StringBuilder Number = new StringBuilder();
		StringBuilder City = new StringBuilder();
		StringBuilder Country = new StringBuilder();


		public String getStreet() {
			return new StringBuilder().append(Street).append(Number).toString().trim();
		}


		public String getCity() {
			return City.toString().trim();
		}


		public String getCountry() {
			return Country.toString().trim();
		}
	}
}

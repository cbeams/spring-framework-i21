package com.interface21.samples.countries.appli;

/**
 * @author Jean-Pierre PAWLAK
 */
public class Country implements ICountry {

	//~ Instance fields --------------------------------------------------------

	private String code;
	private String name;

	//~ Constructors -----------------------------------------------------------

	public Country() {
		super();
	}

	public Country(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}

	//~ Methods ----------------------------------------------------------------

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}
}

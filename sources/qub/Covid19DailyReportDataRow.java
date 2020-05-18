package qub;

public class Covid19DailyReportDataRow
{
    public static final String countyPropertyName = "county";
    public static final String stateOrProvincePropertyName = "stateOrProvince";
    public static final String countryOrRegionPropertyName = "countryOrRegion";
    public static final String confirmedCasesPropertyName = "confirmedCases";

    private String county;
    private String stateOrProvince;
    private String countryOrRegion;
    private Integer confirmedCases;

    private Covid19DailyReportDataRow()
    {
    }

    public static Covid19DailyReportDataRow create()
    {
        return new Covid19DailyReportDataRow();
    }

    public String getCounty()
    {
        return this.county;
    }

    public Covid19DailyReportDataRow setCounty(String county)
    {
        PreCondition.assertNotNullAndNotEmpty(county, "county");

        this.county = county;

        return this;
    }

    public String getStateOrProvince()
    {
        return this.stateOrProvince;
    }

    public Covid19DailyReportDataRow setStateOrProvince(String stateOrProvince)
    {
        PreCondition.assertNotNullAndNotEmpty(stateOrProvince, "stateOrProvince");

        this.stateOrProvince = stateOrProvince;

        return this;
    }

    public String getCountryOrRegion()
    {
        return this.countryOrRegion;
    }

    public Covid19DailyReportDataRow setCountryOrRegion(String countryOrRegion)
    {
        PreCondition.assertNotNullAndNotEmpty(countryOrRegion, "countryOrRegion");

        this.countryOrRegion = countryOrRegion;

        return this;
    }

    public Integer getConfirmedCases()
    {
        return this.confirmedCases;
    }

    public Covid19DailyReportDataRow setConfirmedCases(Integer confirmedCases)
    {
        PreCondition.assertNotNull(confirmedCases, "confirmedCases");
        PreCondition.assertGreaterThanOrEqualTo(confirmedCases, 0, "confirmedCases");

        this.confirmedCases = confirmedCases;

        return this;
    }

    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString(Covid19DailyReportDataRow.countyPropertyName, this.county)
            .setString(Covid19DailyReportDataRow.stateOrProvincePropertyName, this.stateOrProvince)
            .setString(Covid19DailyReportDataRow.countryOrRegionPropertyName, this.countryOrRegion)
            .setString(Covid19DailyReportDataRow.confirmedCasesPropertyName, Strings.escapeAndQuote(this.confirmedCases));
    }

    @Override
    public String toString()
    {
        return this.toJson().toString();
    }

    @Override
    public boolean equals(Object rhs)
    {
        return rhs instanceof Covid19DailyReportDataRow && this.equals((Covid19DailyReportDataRow)rhs);
    }

    public boolean equals(Covid19DailyReportDataRow rhs)
    {
        return rhs != null &&
            Comparer.equal(this.county, rhs.county) &&
            Comparer.equal(this.stateOrProvince, rhs.stateOrProvince) &&
            Comparer.equal(this.countryOrRegion, rhs.countryOrRegion) &&
            Comparer.equal(this.confirmedCases, rhs.confirmedCases);
    }
}

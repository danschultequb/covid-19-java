package qub;

public class Covid19DailyReportDataRow
{
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

    @Override
    public String toString()
    {
        final CharacterList list = CharacterList.create();

        list.add('{');

        Covid19DailyReportDataRow.addProperty(list, "county", this.county);
        Covid19DailyReportDataRow.addProperty(list, "stateOrProvince", this.stateOrProvince);
        Covid19DailyReportDataRow.addProperty(list, "countryOrRegion", this.countryOrRegion);
        Covid19DailyReportDataRow.addProperty(list, "confirmedCases", this.confirmedCases);

        list.add('}');

        final String result = list.toString(true);

        PostCondition.assertNotNullAndNotEmpty(result, "result");

        return result;
    }

    private static void addProperty(CharacterList list, String propertyName, Object propertyValue)
    {
        PreCondition.assertNotNull(list, "list");
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        final String escapedAndQuotedPropertyValue = Strings.escapeAndQuote(propertyValue);
        if (escapedAndQuotedPropertyValue != null)
        {
            if (!list.endsWith('{'))
            {
                list.add(',');
            }

            list.addAll(Strings.escapeAndQuote(propertyName));
            list.add(':');
            list.addAll(escapedAndQuotedPropertyValue);
        }
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

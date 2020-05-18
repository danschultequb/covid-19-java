package qub;

/**
 * A condition that determines whether or not a Covid19DailyReportDataRow pertains to a Covid19Location.
 */
public interface Covid19LocationCondition
{
    /**
     * Get whether or not the provided Covid19DailyReportDataRow applies to a Covid19Location.
     * @param dataRow The data row.
     * @return Whether or not the provided Covid19DailyReportDataRow applies to a Covid19Location.
     */
    boolean matches(Covid19DailyReportDataRow dataRow);

    static Covid19LocationGroupCondition and(Covid19LocationCondition... conditions)
    {
        return Covid19LocationGroupCondition.create(Covid19LocationGroupConditionOperator.And)
            .addAll(conditions);
    }

    static Covid19LocationGroupCondition or(Covid19LocationCondition... conditions)
    {
        return Covid19LocationGroupCondition.create(Covid19LocationGroupConditionOperator.Or)
            .addAll(conditions);
    }

    static Covid19LocationPropertyCondition countryOrRegionEquals(String expectedCountryOrRegion)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.countryOrRegionPropertyName,
            Covid19LocationPropertyConditionOperator.Equals,
            expectedCountryOrRegion);
    }

    static Covid19LocationPropertyCondition countryOrRegionContains(String expectedCountryOrRegion)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.countryOrRegionPropertyName,
            Covid19LocationPropertyConditionOperator.Contains,
            expectedCountryOrRegion);
    }

    static Covid19LocationPropertyCondition stateOrProvinceEquals(String expectedStateOrProvince)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.stateOrProvincePropertyName,
            Covid19LocationPropertyConditionOperator.Equals,
            expectedStateOrProvince);
    }

    static Covid19LocationPropertyCondition stateOrProvinceContains(String expectedStateOrProvince)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.stateOrProvincePropertyName,
            Covid19LocationPropertyConditionOperator.Contains,
            expectedStateOrProvince);
    }

    static Covid19LocationPropertyCondition countyEquals(String expectedCounty)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.countyPropertyName,
            Covid19LocationPropertyConditionOperator.Equals,
            expectedCounty);
    }

    static Covid19LocationPropertyCondition countyContains(String expectedCounty)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.countyPropertyName,
            Covid19LocationPropertyConditionOperator.Contains,
            expectedCounty);
    }
}

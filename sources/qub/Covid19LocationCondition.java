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
    Result<Boolean> matches(Covid19DailyReportDataRow dataRow);

    /**
     * Convert this location condition to its JSON representation.
     * @return The JSON representation of this location condition.
     */
    JSONObject toJson();

    static boolean equals(Covid19LocationCondition lhs, Object rhs)
    {
        PreCondition.assertNotNull(lhs, "lhs");

        return rhs instanceof Covid19LocationCondition && lhs.equals((Covid19LocationCondition)rhs);
    }

    default boolean equals(Covid19LocationCondition rhs)
    {
        return rhs != null &&
            this.getClass().equals(rhs.getClass()) &&
            this.toJson().equals(rhs.toJson());
    }

    static String toString(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        return condition.toJson().toString();
    }

    default String toString(JSONFormat format)
    {
        return this.toJson().toString(format);
    }

    default Result<Integer> toString(CharacterWriteStream stream)
    {
        return this.toJson().toString(stream);
    }

    default Result<Integer> toString(CharacterWriteStream stream, JSONFormat format)
    {
        return this.toJson().toString(stream, format);
    }

    default Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        return this.toJson().toString(stream);
    }

    default Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format)
    {
        return this.toJson().toString(stream, format);
    }

    /**
     * Parse a Covid19LocationCondition from the provided JSONObject.
     * @param json The JSONObject to parse.
     * @return The parsed Covid19LocationCondition.
     */
    static Result<Covid19LocationCondition> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            Covid19LocationCondition result = Covid19LocationPropertyCondition.parse(json).catchError().await();
            if (result == null)
            {
                result = Covid19LocationGroupCondition.parse(json).catchError().await();
            }

            if (result == null)
            {
                throw new ParseException("Unrecognized Covid19LocationCondition: " + json.toString());
            }

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    static Covid19LocationGroupCondition and(Covid19LocationCondition... conditions)
    {
        return Covid19LocationGroupCondition.create(Covid19LocationGroupConditionOperator.And)
            .addConditions(conditions);
    }

    static Covid19LocationGroupCondition or(Covid19LocationCondition... conditions)
    {
        return Covid19LocationGroupCondition.create(Covid19LocationGroupConditionOperator.Or)
            .addConditions(conditions);
    }

    static Covid19LocationPropertyCondition countryOrRegionEquals(String expectedCountryOrRegion)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.countryOrRegionPropertyName,
            Covid19LocationPropertyConditionOperator.Equals,
            expectedCountryOrRegion);
    }

    static Covid19LocationPropertyCondition countryOrRegionContains(String expectedCountryOrRegionSubstring)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.countryOrRegionPropertyName,
            Covid19LocationPropertyConditionOperator.Contains,
            expectedCountryOrRegionSubstring);
    }

    static Covid19LocationPropertyCondition stateOrProvinceEquals(String expectedStateOrProvince)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.stateOrProvincePropertyName,
            Covid19LocationPropertyConditionOperator.Equals,
            expectedStateOrProvince);
    }

    static Covid19LocationPropertyCondition stateOrProvinceContains(String expectedStateOrProvinceSubstring)
    {
        return Covid19LocationPropertyCondition.create(
            Covid19DailyReportDataRow.stateOrProvincePropertyName,
            Covid19LocationPropertyConditionOperator.Contains,
            expectedStateOrProvinceSubstring);
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

package qub;

public class Covid19LocationPropertyCondition implements Covid19LocationCondition
{
    private static final String propertyNamePropertyName = "propertyName";
    private static final String operatorPropertyName = "operator";
    private static final String expectedPropertyValuePropertyName = "expectedPropertyValue";

    private static final Map<String,Function1<Covid19DailyReportDataRow,Object>> propertyGetters = ListMap.<String,Function1<Covid19DailyReportDataRow,Object>>create(Comparer::equalIgnoreCase)
        .set(Covid19DailyReportDataRow.stateOrProvincePropertyName, Covid19DailyReportDataRow::getStateOrProvince)
        .set(Covid19DailyReportDataRow.countryOrRegionPropertyName, Covid19DailyReportDataRow::getCountryOrRegion)
        .set(Covid19DailyReportDataRow.countyPropertyName, Covid19DailyReportDataRow::getCounty)
        .set(Covid19DailyReportDataRow.confirmedCasesPropertyName, Covid19DailyReportDataRow::getConfirmedCases);

    private final String propertyName;
    private final Function1<Covid19DailyReportDataRow,Object> propertyGetter;
    private final Object expectedPropertyValue;
    private final Covid19LocationPropertyConditionOperator operator;

    private Covid19LocationPropertyCondition(String propertyName, Covid19LocationPropertyConditionOperator operator, Object expectedPropertyValue)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertOneOf(propertyName, Covid19LocationPropertyCondition.propertyGetters.getKeys(), "propertyName");
        PreCondition.assertNotNull(operator, "operator");

        this.propertyName = propertyName;
        this.propertyGetter = Covid19LocationPropertyCondition.propertyGetters.get(propertyName).await();
        this.operator = operator;
        this.expectedPropertyValue = expectedPropertyValue;
    }

    public static Covid19LocationPropertyCondition create(String propertyName, Covid19LocationPropertyConditionOperator operator, Object expectedPropertyValue)
    {
        return new Covid19LocationPropertyCondition(propertyName, operator, expectedPropertyValue);
    }

    public String getPropertyName()
    {
        return this.propertyName;
    }

    public Covid19LocationPropertyConditionOperator getOperator()
    {
        return this.operator;
    }

    public Object getExpectedPropertyValue()
    {
        return this.expectedPropertyValue;
    }

    @Override
    public boolean matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        final Object propertyValue = this.propertyGetter.run(dataRow);
        boolean result = Comparer.equal(propertyValue, expectedPropertyValue);
        if (!result && expectedPropertyValue != null && propertyValue != null)
        {
            if (expectedPropertyValue instanceof String)
            {
                final String expectedPropertyValueString = ((String)expectedPropertyValue).toLowerCase();
                final String propertyValueString = propertyValue.toString().toLowerCase();
                if (this.operator == Covid19LocationPropertyConditionOperator.Equals)
                {
                    result = propertyValueString.equals(expectedPropertyValueString);
                }
                else if (this.operator == Covid19LocationPropertyConditionOperator.Contains)
                {
                    result = propertyValueString.contains(expectedPropertyValueString);
                }
            }
        }

        return result;
    }

    @Override
    public JSONObject toJson()
    {
        final JSONObject result = JSONObject.create()
            .setString(Covid19LocationPropertyCondition.propertyNamePropertyName, this.propertyName)
            .setString(Covid19LocationPropertyCondition.operatorPropertyName, this.operator.toString());

        JSONSegment expectedPropertyValueSegment;
        if (this.expectedPropertyValue == null)
        {
            expectedPropertyValueSegment = JSONNull.segment;
        }
        else if (this.expectedPropertyValue instanceof Number)
        {
            expectedPropertyValueSegment = JSONNumber.get(((Number)this.expectedPropertyValue).doubleValue());
        }
        else
        {
            expectedPropertyValueSegment = JSONString.get(this.expectedPropertyValue.toString());
        }
        result.set(Covid19LocationPropertyCondition.expectedPropertyValuePropertyName, expectedPropertyValueSegment);

        PostCondition.assertNotNull(result, "result");

        return result;
    }
}

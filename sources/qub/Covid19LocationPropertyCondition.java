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

    private final JSONObject json;

    private Covid19LocationPropertyCondition(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
    }

    public static Covid19LocationPropertyCondition create()
    {
        return new Covid19LocationPropertyCondition(JSONObject.create());
    }

    public static Covid19LocationPropertyCondition create(String propertyName, Covid19LocationPropertyConditionOperator operator, Object expectedPropertyValue)
    {
        return Covid19LocationPropertyCondition.create()
            .setPropertyName(propertyName)
            .setOperator(operator)
            .setExpectedPropertyValue(expectedPropertyValue);
    }

    public static Result<Covid19LocationPropertyCondition> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final Covid19LocationPropertyCondition result = new Covid19LocationPropertyCondition(json);
            result.getPropertyName().await();
            result.getOperator().await();
            result.getExpectedPropertyValue().await();
            return result;
        });
    }

    public Result<String> getPropertyName()
    {
        return this.json.getString(Covid19LocationPropertyCondition.propertyNamePropertyName);
    }

    public Covid19LocationPropertyCondition setPropertyName(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");

        this.json.setString(Covid19LocationPropertyCondition.propertyNamePropertyName, propertyName);

        return this;
    }

    public Result<Covid19LocationPropertyConditionOperator> getOperator()
    {
        return this.json.getString(Covid19LocationPropertyCondition.operatorPropertyName)
            .then((String operatorString) ->
            {
                return Enums.parse(Covid19LocationPropertyConditionOperator.class, operatorString).await();
            });
    }

    public Covid19LocationPropertyCondition setOperator(Covid19LocationPropertyConditionOperator operator)
    {
        PreCondition.assertNotNull(operator, "operator");

        this.json.setString(Covid19LocationPropertyCondition.operatorPropertyName, operator.toString());

        return this;
    }

    public Result<Object> getExpectedPropertyValue()
    {
        return Result.create(() ->
        {
            Object result;

            final JSONSegment expectedPropertySegment = this.json.get(Covid19LocationPropertyCondition.expectedPropertyValuePropertyName).await();
            if (expectedPropertySegment instanceof JSONNull)
            {
                result = null;
            }
            else if (expectedPropertySegment instanceof JSONString)
            {
                result = ((JSONString)expectedPropertySegment).getValue();
            }
            else if (expectedPropertySegment instanceof JSONNumber)
            {
                result = ((JSONNumber)expectedPropertySegment).getValue();
            }
            else
            {
                throw new ParseException("Unexpected expected property value type: " + Types.getTypeName(expectedPropertySegment) + "(" + expectedPropertySegment.toString() + ")");
            }

            return result;
        });
    }

    public Covid19LocationPropertyCondition setExpectedPropertyValue(Object expectedPropertyValue)
    {
        PreCondition.assertTrue(expectedPropertyValue == null || expectedPropertyValue instanceof Number || expectedPropertyValue instanceof String, "expectedPropertyValue == null || expectedPropertyValue instanceof Number || expectedPropertyValue instanceof String");

        JSONSegment expectedPropertySegment;
        if (expectedPropertyValue == null)
        {
            expectedPropertySegment = JSONNull.segment;
        }
        else
        {
            if (expectedPropertyValue instanceof Number)
            {
                expectedPropertySegment = JSONNumber.get(expectedPropertyValue.toString());
            }
            else // if (expectedPropertyValue instanceof String)
            {
                expectedPropertySegment = JSONString.get(expectedPropertyValue.toString());
            }
        }
        this.json.set(Covid19LocationPropertyCondition.expectedPropertyValuePropertyName, expectedPropertySegment);

        return this;
    }

    @Override
    public Result<Boolean> matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        return Result.create(() ->
        {
            final String propertyName = this.getPropertyName().await();
            final Function1<Covid19DailyReportDataRow,Object> propertyGetter = Covid19LocationPropertyCondition.propertyGetters.get(propertyName).await();
            final Covid19LocationPropertyConditionOperator operator = this.getOperator().await();
            final Object expectedPropertyValue = this.getExpectedPropertyValue().await();

            final Object propertyValue = propertyGetter.run(dataRow);

            boolean result;
            if (expectedPropertyValue instanceof String)
            {
                final String expectedPropertyValueString = (String)expectedPropertyValue;
                if (operator == Covid19LocationPropertyConditionOperator.Contains)
                {
                    result = propertyValue != null && propertyValue.toString().contains(expectedPropertyValueString);
                }
                else // if (operator == Covid19LocationPropertyConditionOperator.Equals)
                {
                    result = Comparer.equal(propertyValue, expectedPropertyValue);
                }
            }
            else if (expectedPropertyValue instanceof Number)
            {
                if (operator == Covid19LocationPropertyConditionOperator.Equals)
                {
                    result = Comparer.equal(propertyValue, expectedPropertyValue);
                }
                else
                {
                    result = false;
                }
            }
            else
            {
                result = (propertyValue == expectedPropertyValue);
            }

            return result;
        });
    }

    @Override
    public JSONObject toJson()
    {
        return this.json;
    }

    @Override
    public String toString()
    {
        return Covid19LocationCondition.toString(this);
    }

    @Override
    public boolean equals(Object rhs)
    {
        return Covid19LocationCondition.equals(this, rhs);
    }
}

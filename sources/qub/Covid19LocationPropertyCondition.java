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
    private String propertyName;
    private Function1<Covid19DailyReportDataRow,Object> propertyGetter;
    private Covid19LocationPropertyConditionOperator operator;
    private Object expectedPropertyValue;

    private Covid19LocationPropertyCondition(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
    }

    public static Covid19LocationPropertyCondition create(String propertyName, Covid19LocationPropertyConditionOperator operator, Object expectedPropertyValue)
    {
        return new Covid19LocationPropertyCondition(JSONObject.create())
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

            result.propertyName = json.getString(Covid19LocationPropertyCondition.propertyNamePropertyName).await();
            result.propertyGetter = Covid19LocationPropertyCondition.propertyGetters.get(result.propertyName)
                .convertError(NotFoundException.class, () -> new ParseException("Unrecognized property name: " + Strings.escapeAndQuote(result.propertyName)))
                .await();

            result.operator = json.getString(Covid19LocationPropertyCondition.operatorPropertyName)
                .then((String operatorString) -> Enums.parse(Covid19LocationPropertyConditionOperator.class, operatorString).await())
                .await();

            final JSONSegment expectedPropertySegment = json.get(Covid19LocationPropertyCondition.expectedPropertyValuePropertyName).await();
            Object expectedPropertyValue;
            if (expectedPropertySegment instanceof JSONNull)
            {
                expectedPropertyValue = null;
            }
            else if (expectedPropertySegment instanceof JSONString)
            {
                expectedPropertyValue = ((JSONString)expectedPropertySegment).getValue();
            }
            else if (expectedPropertySegment instanceof JSONNumber)
            {
                expectedPropertyValue = ((JSONNumber)expectedPropertySegment).getValue();
            }
            else
            {
                throw new ParseException("Unexpected expected property value type: " + Types.getTypeName(expectedPropertySegment) + "(" + expectedPropertySegment.toString() + ")");
            }
            result.expectedPropertyValue = expectedPropertyValue;

            return result;
        });
    }

    public String getPropertyName()
    {
        return this.propertyName;
    }

    public Covid19LocationPropertyCondition setPropertyName(String propertyName)
    {
        PreCondition.assertNotNullAndNotEmpty(propertyName, "propertyName");
        PreCondition.assertOneOf(propertyName, Covid19LocationPropertyCondition.propertyGetters.getKeys(), "propertyName");

        this.json.setString(Covid19LocationPropertyCondition.propertyNamePropertyName, propertyName);
        this.propertyName = propertyName;
        this.propertyGetter = Covid19LocationPropertyCondition.propertyGetters.get(propertyName).await();

        return this;
    }

    public Covid19LocationPropertyConditionOperator getOperator()
    {
        return this.operator;
    }

    public Covid19LocationPropertyCondition setOperator(Covid19LocationPropertyConditionOperator operator)
    {
        PreCondition.assertNotNull(operator, "operator");

        this.json.setString(Covid19LocationPropertyCondition.operatorPropertyName, operator.toString());
        this.operator = operator;

        return this;
    }

    public Object getExpectedPropertyValue()
    {
        return this.expectedPropertyValue;
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
        this.expectedPropertyValue = expectedPropertyValue;

        return this;
    }

    @Override
    public boolean matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        final Object propertyValue = this.propertyGetter.run(dataRow);

        boolean result;
        if (this.expectedPropertyValue instanceof String)
        {
            final String expectedPropertyValueString = (String)this.expectedPropertyValue;
            if (this.operator == Covid19LocationPropertyConditionOperator.Contains)
            {
                result = propertyValue != null && propertyValue.toString().contains(expectedPropertyValueString);
            }
            else // if (operator == Covid19LocationPropertyConditionOperator.Equals)
            {
                result = Comparer.equal(propertyValue, this.expectedPropertyValue);
            }
        }
        else if (this.expectedPropertyValue instanceof Number)
        {
            if (this.operator == Covid19LocationPropertyConditionOperator.Equals)
            {
                result = Comparer.equal(propertyValue, this.expectedPropertyValue);
            }
            else
            {
                result = false;
            }
        }
        else
        {
            result = (propertyValue == this.expectedPropertyValue);
        }

        return result;
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

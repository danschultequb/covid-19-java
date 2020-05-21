package qub;

public class Covid19LocationGroupCondition implements Covid19LocationCondition
{
    public static final String operatorPropertyName = "operator";
    public static final String conditionsPropertyName = "conditions";

    private final JSONObject json;

    private Covid19LocationGroupCondition(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
    }

    public static Covid19LocationGroupCondition create()
    {
        return new Covid19LocationGroupCondition(JSONObject.create());
    }

    public static Covid19LocationGroupCondition create(Covid19LocationGroupConditionOperator operator, Covid19LocationGroupCondition... conditions)
    {
        return Covid19LocationGroupCondition.create()
            .setOperator(operator)
            .addConditions(conditions);
    }

    public static Result<Covid19LocationGroupCondition> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final Covid19LocationGroupCondition result = new Covid19LocationGroupCondition(json);
            result.getOperator().await();
            result.getConditions().await();
            return result;
        });
    }

    /**
     * Get the operator that this condition will use when combining the group's results.
     * @return The operator that this condition will use when combining the group's results.
     */
    public Result<Covid19LocationGroupConditionOperator> getOperator()
    {
        return Result.create(() ->
        {
            final String operatorString = this.json.getString(Covid19LocationGroupCondition.operatorPropertyName).await();
            return Enums.parse(Covid19LocationGroupConditionOperator.class, operatorString).await();
        });
    }

    public Covid19LocationGroupCondition setOperator(Covid19LocationGroupConditionOperator operator)
    {
        PreCondition.assertNotNull(operator, "operator");

        this.json.setString(Covid19LocationGroupCondition.operatorPropertyName, operator.toString());

        return this;
    }

    private Result<JSONArray> getConditionsArray()
    {
        return this.json.getArray(Covid19LocationGroupCondition.conditionsPropertyName)
            .catchError(NotFoundException.class, () ->
            {
                final JSONArray newConditionsArray = JSONArray.create();
                this.json.setArray(Covid19LocationGroupCondition.conditionsPropertyName, newConditionsArray);
                return newConditionsArray;
            });
    }

    /**
     * Get the conditions whose matches() results will be combined using this group condition's
     * operator.
     * @return The conditions whose matches() results will be combined using this group condition's
     * operator.
     */
    public Result<Iterable<Covid19LocationCondition>> getConditions()
    {
        return Result.create(() ->
        {
            final JSONArray conditionsArray = this.getConditionsArray().await();
            return conditionsArray
                .instanceOf(JSONObject.class)
                .map((JSONObject conditionJsonObject) -> Covid19LocationCondition.parse(conditionJsonObject).await())
                .toList();
        });
    }

    public Covid19LocationGroupCondition addCondition(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        this.getConditionsArray().await()
            .add(condition.toJson());

        return this;
    }

    public Covid19LocationGroupCondition addConditions(Covid19LocationCondition... conditions)
    {
        PreCondition.assertNotNull(conditions, "conditions");

        return this.addConditions(Iterable.create(conditions));
    }

    public Covid19LocationGroupCondition addConditions(Iterable<Covid19LocationCondition> conditions)
    {
        PreCondition.assertNotNull(conditions, "conditions");

        for (final Covid19LocationCondition condition : conditions)
        {
            this.addCondition(condition);
        }

        return this;
    }

    @Override
    public Result<Boolean> matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        return Result.create(() ->
        {
            boolean result = (this.getOperator().await() == Covid19LocationGroupConditionOperator.And);
            final boolean startState = result;

            for (final Covid19LocationCondition condition : this.getConditions().await())
            {
                result = condition.matches(dataRow).await();
                if (result != startState)
                {
                    break;
                }
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

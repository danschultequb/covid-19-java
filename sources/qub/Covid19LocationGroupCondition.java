package qub;

public class Covid19LocationGroupCondition implements Covid19LocationCondition
{
    public static final String operatorPropertyName = "operator";
    public static final String conditionsPropertyName = "conditions";

    private final JSONObject json;
    private Covid19LocationGroupConditionOperator operator;
    private final List<Covid19LocationCondition> conditions;

    private Covid19LocationGroupCondition(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
        this.conditions = List.create();
    }

    public static Covid19LocationGroupCondition create(Covid19LocationGroupConditionOperator operator, Covid19LocationGroupCondition... conditions)
    {
        return new Covid19LocationGroupCondition(JSONObject.create())
            .setOperator(operator)
            .addConditions(conditions);
    }

    public static Result<Covid19LocationGroupCondition> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final Covid19LocationGroupCondition result = new Covid19LocationGroupCondition(json);

            final String operatorString = json.getString(Covid19LocationGroupCondition.operatorPropertyName).await();
            result.operator = Enums.parse(Covid19LocationGroupConditionOperator.class, operatorString).await();

            final JSONArray conditionsArray = json.getArrayOrNull(Covid19LocationGroupCondition.conditionsPropertyName)
                .catchError(NotFoundException.class)
                .await();
            if (conditionsArray != null)
            {
                result.conditions.addAll(conditionsArray
                    .instanceOf(JSONObject.class)
                    .map((JSONObject conditionJsonObject) -> Covid19LocationCondition.parse(conditionJsonObject).await())
                    .toList());
            }

            return result;
        });
    }

    /**
     * Get the operator that this condition will use when combining the group's results.
     * @return The operator that this condition will use when combining the group's results.
     */
    public Covid19LocationGroupConditionOperator getOperator()
    {
        return this.operator;
    }

    public Covid19LocationGroupCondition setOperator(Covid19LocationGroupConditionOperator operator)
    {
        PreCondition.assertNotNull(operator, "operator");

        this.json.setString(Covid19LocationGroupCondition.operatorPropertyName, operator.toString());
        this.operator = operator;

        return this;
    }

    /**
     * Get the conditions whose matches() results will be combined using this group condition's
     * operator.
     * @return The conditions whose matches() results will be combined using this group condition's
     * operator.
     */
    public Iterable<Covid19LocationCondition> getConditions()
    {
        return this.conditions;
    }

    public Covid19LocationGroupCondition addCondition(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        JSONArray conditionsArray = this.json.getArrayOrNull(Covid19LocationGroupCondition.conditionsPropertyName)
            .catchError(NotFoundException.class)
            .await();
        if (conditionsArray == null)
        {
            conditionsArray = JSONArray.create();
            this.json.setArray(Covid19LocationGroupCondition.conditionsPropertyName, conditionsArray);
        }

        conditionsArray.add(condition.toJson());
        this.conditions.add(condition);

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
    public boolean matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        boolean result = (this.getOperator() == Covid19LocationGroupConditionOperator.And);
        final boolean startState = result;

        for (final Covid19LocationCondition condition : this.getConditions())
        {
            result = condition.matches(dataRow);
            if (result != startState)
            {
                break;
            }
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

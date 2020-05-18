package qub;

public class Covid19LocationGroupCondition implements Covid19LocationCondition
{
    private final Covid19LocationGroupConditionOperator operator;
    private final List<Covid19LocationCondition> conditions;

    private Covid19LocationGroupCondition(Covid19LocationGroupConditionOperator operator)
    {
        PreCondition.assertNotNull(operator, "operator");

        this.operator = operator;
        this.conditions = List.create();
    }

    public static Covid19LocationGroupCondition create(Covid19LocationGroupConditionOperator operator)
    {
        return new Covid19LocationGroupCondition(operator);
    }

    public Covid19LocationGroupCondition add(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        this.conditions.add(condition);

        return this;
    }

    public Covid19LocationGroupCondition addAll(Covid19LocationCondition... conditions)
    {
        PreCondition.assertNotNull(conditions, "conditions");

        for (final Covid19LocationCondition condition : conditions)
        {
            this.add(condition);
        }

        return this;
    }

    @Override
    public boolean matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        boolean result = (this.operator == Covid19LocationGroupConditionOperator.And);
        final boolean startState = result;

        for (final Covid19LocationCondition condition : this.conditions)
        {
            result = condition.matches(dataRow);
            if (result != startState)
            {
                break;
            }
        }

        return result;
    }
}

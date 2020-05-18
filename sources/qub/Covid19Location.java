package qub;

public class Covid19Location
{
    private final String name;
    private Covid19LocationCondition condition;

    private Covid19Location(String name)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        this.name = name;
        this.condition = (Covid19DailyReportDataRow dataRow) -> true;
    }

    public static Covid19Location create(String name)
    {
        return new Covid19Location(name);
    }

    public String getName()
    {
        return this.name;
    }

    public Covid19Location setCondition(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        this.condition = condition;

        return this;
    }

    public boolean matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        return this.condition.matches(dataRow);
    }
}

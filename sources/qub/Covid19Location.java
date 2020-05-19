package qub;

public class Covid19Location
{
    public static final String namePropertyName = "name";
    public static final String conditionPropertyName = "condition";

    private final String name;
    private Covid19LocationCondition condition;

    private Covid19Location(String name)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        this.name = name;
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

        return this.condition == null || this.condition.matches(dataRow);
    }

    public JSONObject toJson()
    {
        return JSONObject.create()
            .setString(Covid19Location.namePropertyName, this.name)
            .set(Covid19Location.conditionPropertyName, this.condition == null
                ? JSONNull.segment
                : this.condition.toJson());
    }
}

package qub;

public class Covid19Location
{
    private final JSONProperty json;
    private final Covid19LocationCondition condition;

    private Covid19Location(JSONProperty json, Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
        this.condition = condition;
    }

    public static Covid19Location create(String name)
    {
        return Covid19Location.create(name, null);
    }

    public static Covid19Location create(String name, Covid19LocationCondition condition)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        final JSONObject conditionJson = (condition == null ? JSONObject.create() : condition.toJson());
        final JSONProperty jsonProperty = JSONProperty.create(name, conditionJson);
        final Covid19Location result = new Covid19Location(jsonProperty, condition);

        PostCondition.assertNotNull(result, "result");
        PostCondition.assertEqual(name, result.getName(), "result.getName()");
        PostCondition.assertEqual(condition, result.getCondition(), "result.getCondition()");

        return result;
    }

    public static Result<Covid19Location> parse(JSONProperty json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final Covid19Location result = new Covid19Location(
                json,
                Covid19LocationCondition.parse(json.getObjectValue().await()).await());

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    public String getName()
    {
        return this.json.getName();
    }

    public Covid19LocationCondition getCondition()
    {
        return this.condition;
    }

    public boolean matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        final Covid19LocationCondition condition = this.getCondition();
        return condition == null || condition.matches(dataRow);
    }

    @Override
    public String toString()
    {
        return this.json.toString();
    }

    public Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format)
    {
        return this.json.toString(stream, format);
    }

    public JSONProperty toJson()
    {
        return this.json;
    }
}

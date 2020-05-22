package qub;

public class Covid19Location implements JSONSegment
{
    public static final String namePropertyName = "name";
    public static final String conditionPropertyName = "condition";

    private final JSONObject json;
    private String name;
    private Covid19LocationCondition condition;

    private Covid19Location(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
    }

    public static Covid19Location create(String name)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        final Covid19Location result = new Covid19Location(JSONObject.create())
            .setName(name);

        PostCondition.assertNotNull(result, "result");
        PostCondition.assertEqual(name, result.getName(), "result.getName()");

        return result;
    }

    public static Result<Covid19Location> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final Covid19Location result = new Covid19Location(json);
            result.name = json.getString(Covid19Location.namePropertyName).await();
            result.condition = Covid19LocationCondition.parse(
                json.getObjectOrNull(Covid19Location.conditionPropertyName)
                    .catchError(NotFoundException.class)
                    .await())
                .await();

            PostCondition.assertNotNull(result, "result");

            return result;
        });
    }

    public String getName()
    {
        return this.name;
    }

    public Covid19Location setName(String name)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        this.json.setString(Covid19Location.namePropertyName, name);
        this.name = name;

        return this;
    }

    public Covid19LocationCondition getCondition()
    {
        return this.condition;
    }

    public Covid19Location setCondition(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        this.json.set(Covid19Location.conditionPropertyName, condition.toJson());
        this.condition = condition;

        return this;
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

    public String toString(JSONFormat format)
    {
        return this.json.toString(format);
    }

    public Result<Integer> toString(CharacterWriteStream stream)
    {
        return this.json.toString(stream);
    }

    public Result<Integer> toString(IndentedCharacterWriteStream stream)
    {
        return this.json.toString(stream);
    }

    public Result<Integer> toString(CharacterWriteStream stream, JSONFormat format)
    {
        return this.json.toString(stream, format);
    }

    public Result<Integer> toString(IndentedCharacterWriteStream stream, JSONFormat format)
    {
        return this.json.toString(stream, format);
    }

    public JSONObject toJson()
    {
        return this.json;
    }
}

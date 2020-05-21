package qub;

public class Covid19Location implements JSONSegment
{
    public static final String namePropertyName = "name";
    public static final String conditionPropertyName = "condition";

    private final JSONObject json;

    private Covid19Location(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
    }

    public static Covid19Location create()
    {
        return new Covid19Location(JSONObject.create());
    }

    public static Result<Covid19Location> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        return Result.create(() ->
        {
            final Covid19Location result = new Covid19Location(json);
            result.getName().await();
            return result;
        });
    }

    public static Covid19Location create(String name)
    {
        return Covid19Location.create()
            .setName(name);
    }

    public Result<String> getName()
    {
        return this.json.getString(Covid19Location.namePropertyName);
    }

    public Covid19Location setName(String name)
    {
        PreCondition.assertNotNullAndNotEmpty(name, "name");

        this.json.setString(Covid19Location.namePropertyName, name);

        return this;
    }

    public Result<Covid19LocationCondition> getCondition()
    {
        return Result.create(() ->
        {
            final JSONObject conditionJson = this.json.getObjectOrNull(Covid19Location.conditionPropertyName)
                .catchError(NotFoundException.class)
                .await();
            return conditionJson == null ? null : Covid19LocationCondition.parse(conditionJson).await();
        });
    }

    public Covid19Location setCondition(Covid19LocationCondition condition)
    {
        PreCondition.assertNotNull(condition, "condition");

        this.json.set(Covid19Location.conditionPropertyName, condition.toJson());

        return this;
    }

    public Result<Boolean> matches(Covid19DailyReportDataRow dataRow)
    {
        PreCondition.assertNotNull(dataRow, "dataRow");

        return Result.create(() ->
        {
            final Covid19LocationCondition condition = this.getCondition().await();
            return condition == null || condition.matches(dataRow).await();
        });
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

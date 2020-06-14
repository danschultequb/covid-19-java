package qub;

public class Covid19Configuration
{
    public static final String locationsPropertyName = "locations";

    private final JSONObject json;
    private final List<Covid19Location> locations;

    private Covid19Configuration(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
        this.locations = List.create();
    }

    public static Covid19Configuration create()
    {
        return new Covid19Configuration(JSONObject.create());
    }

    public static Result<Covid19Configuration> parse(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return Result.create(() ->
        {
            Covid19Configuration result;
            try (final ByteReadStream byteReadStream = BufferedByteReadStream.create(file.getContentReadStream().await()))
            {
                result = Covid19Configuration.parse(byteReadStream).await();
            }
            return result;
        });
    }

    public static Result<Covid19Configuration> parse(ByteReadStream byteReadStream)
    {
        PreCondition.assertNotNull(byteReadStream, "byteReadStream");

        return Covid19Configuration.parse(CharacterReadStream.create(byteReadStream));
    }

    public static Result<Covid19Configuration> parse(CharacterReadStream characterReadStream)
    {
        PreCondition.assertNotNull(characterReadStream, "characterReadStream");

        return Covid19Configuration.parse(CharacterReadStream.iterate(characterReadStream));
    }

    public static Result<Covid19Configuration> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Result.create(() ->
        {
            final JSONObject json = JSON.parseObject(characters).await();
            return Covid19Configuration.parse(json).await();
        });
    }

    public static Result<Covid19Configuration> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");
        return Result.create(() ->
        {
            final Covid19Configuration result = new Covid19Configuration(json);

            final JSONObject locationsObject = json.getObjectOrNull(Covid19Configuration.locationsPropertyName)
                .catchError(NotFoundException.class)
                .await();
            if (locationsObject != null)
            {
                result.locations.addAll(locationsObject.getProperties()
                    .map((JSONProperty locationJson) -> Covid19Location.parse(locationJson).await()));
            }

            return result;
        });
    }

    public Iterable<Covid19Location> getLocations()
    {
        return this.locations;
    }

    public Covid19Configuration addLocation(Covid19Location location)
    {
        PreCondition.assertNotNull(location, "location");

        JSONObject locationsJson = this.json.getObjectOrNull(Covid19Configuration.locationsPropertyName)
            .catchError(NotFoundException.class)
            .await();
        if (locationsJson == null)
        {
            locationsJson = JSONObject.create();
            this.json.setObject(Covid19Configuration.locationsPropertyName, locationsJson);
        }
        locationsJson.set(location.toJson());
        this.locations.add(location);

        return this;
    }

    public Covid19Configuration addLocations(Covid19Location... locations)
    {
        PreCondition.assertNotNull(locations, "locations");

        return this.addLocations(Iterable.create(locations));
    }

    public Covid19Configuration addLocations(Iterable<Covid19Location> locations)
    {
        PreCondition.assertNotNull(locations, "locations");

        for (final Covid19Location location : locations)
        {
            this.addLocation(location);
        }

        return this;
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

package qub;

public class QubCovid19Configuration
{
    public static final String locationsPropertyName = "locations";

    private final JSONObject json;
    private final List<Covid19Location> locations;

    private QubCovid19Configuration(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");

        this.json = json;
        this.locations = List.create();
    }

    public static QubCovid19Configuration create()
    {
        return new QubCovid19Configuration(JSONObject.create());
    }

    public static Result<QubCovid19Configuration> parse(File file)
    {
        PreCondition.assertNotNull(file, "file");

        return Result.create(() ->
        {
            QubCovid19Configuration result;
            try (final ByteReadStream byteReadStream = BufferedByteReadStream.create(file.getContentReadStream().await()))
            {
                result = QubCovid19Configuration.parse(byteReadStream).await();
            }
            return result;
        });
    }

    public static Result<QubCovid19Configuration> parse(ByteReadStream byteReadStream)
    {
        PreCondition.assertNotNull(byteReadStream, "byteReadStream");

        return QubCovid19Configuration.parse(CharacterReadStream.create(byteReadStream));
    }

    public static Result<QubCovid19Configuration> parse(CharacterReadStream characterReadStream)
    {
        PreCondition.assertNotNull(characterReadStream, "characterReadStream");

        return QubCovid19Configuration.parse(CharacterReadStream.iterate(characterReadStream));
    }

    public static Result<QubCovid19Configuration> parse(Iterator<Character> characters)
    {
        PreCondition.assertNotNull(characters, "characters");

        return Result.create(() ->
        {
            final JSONObject json = JSON.parseObject(characters).await();
            return QubCovid19Configuration.parse(json).await();
        });
    }

    public static Result<QubCovid19Configuration> parse(JSONObject json)
    {
        PreCondition.assertNotNull(json, "json");
        return Result.create(() ->
        {
            final QubCovid19Configuration result = new QubCovid19Configuration(json);

            final JSONArray locationsArray = json.getArrayOrNull(QubCovid19Configuration.locationsPropertyName)
                .catchError(NotFoundException.class)
                .await();
            if (locationsArray != null)
            {
                result.locations.addAll(locationsArray
                    .instanceOf(JSONObject.class)
                    .map((JSONObject locationJson) -> Covid19Location.parse(locationJson).await()));
            }

            return result;
        });
    }

    public Iterable<Covid19Location> getLocations()
    {
        return this.locations;
    }

    public QubCovid19Configuration addLocation(Covid19Location location)
    {
        PreCondition.assertNotNull(location, "location");

        JSONArray locationsJson = this.json.getArrayOrNull(QubCovid19Configuration.locationsPropertyName)
            .catchError(NotFoundException.class)
            .await();
        if (locationsJson == null)
        {
            locationsJson = JSONArray.create();
            this.json.setArray(QubCovid19Configuration.locationsPropertyName, locationsJson);
        }
        locationsJson.add(location.toJson());
        this.locations.add(location);

        return this;
    }

    public QubCovid19Configuration addLocations(Covid19Location... locations)
    {
        PreCondition.assertNotNull(locations, "locations");

        return this.addLocations(Iterable.create(locations));
    }

    public QubCovid19Configuration addLocations(Iterable<Covid19Location> locations)
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

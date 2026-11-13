package com.tableturn.error;

/** The example error bodies the API docs show for each refusal - the exact shape a client will receive. */
public final class ProblemExamples {

    public static final String PROBLEM = "application/problem+json";

        public static final String VALIDATION_QUERY = """
            {"type":"/errors/validation","title":"Invalid request","status":400,"detail":"The request is not valid",\
            "instance":"/branches/Baner/tables","errors":[{"field":"section",\
            "message":"'Roof' is not an allowed value"}]}""";
                public static final String VALIDATION_JOIN = """
            {"type":"/errors/validation","title":"Invalid request","status":400,"detail":"The request is not valid",\
            "instance":"/waitlist","errors":[{"field":"name","message":"must not be blank"}]}""";
                public static final String VALIDATION_SEAT = """
            {"type":"/errors/validation","title":"Invalid request","status":400,"detail":"The request is not valid",\
            "instance":"/seatings","errors":[{"field":"table","message":"must not be blank"}]}""";
                public static final String NO_SUCH_BRANCH = """
            {"type":"/errors/no-such-branch","title":"No such branch","status":404,\
            "detail":"There is no branch Kothrud","instance":"/branches/Kothrud/tables"}""";
                public static final String NO_SUCH_TABLE = """
            {"type":"/errors/no-such-table","title":"No such table","status":404,\
            "detail":"There is no table T99 in Baner","instance":"/branches/Baner/tables/T99"}""";
                public static final String PARTY_TOO_BIG = """
            {"type":"/errors/party-too-big","title":"Party too big","status":400,\
            "detail":"No table here seats 9; the largest seats 6","instance":"/waitlist","maxCovers":6}""";
                public static final String NO_SUCH_PARTY = """
            {"type":"/errors/no-such-party","title":"No such party","status":404,\
            "detail":"There is no party W999 on this list","instance":"/parties/W999"}""";
                public static final String TABLE_TOO_SMALL = """
            {"type":"/errors/table-too-small","title":"Table too small","status":409,\
            "detail":"Table T11 seats 4; the party is 6","instance":"/seatings"}""";
                public static final String ALREADY_SEATED = """
            {"type":"/errors/already-seated","title":"Already seated","status":409,\
            "detail":"Party W031 is already at a table","instance":"/seatings"}""";
                public static final String TABLE_NOT_CLEARED = """
            {"type":"/errors/table-not-cleared","title":"Table not cleared","status":409,\
            "detail":"Table T12 has not been cleared","instance":"/seatings"}""";
                public static final String NO_SUCH_PARTY_SEAT = """
            {"type":"/errors/no-such-party","title":"No such party","status":404,\
            "detail":"There is no party W999 on this list","instance":"/seatings"}""";
                public static final String NO_SUCH_TABLE_SEAT = """
            {"type":"/errors/no-such-table","title":"No such table","status":404,\
            "detail":"There is no table T99 in Baner","instance":"/seatings"}""";
                public static final String ALREADY_LEFT = """
            {"type":"/errors/already-left","title":"Already left","status":409,\
            "detail":"The party at seating S204 has already left","instance":"/seatings/S204/leave"}""";
                public static final String NOT_LEFT_YET = """
            {"type":"/errors/not-left-yet","title":"Not left yet","status":409,\
            "detail":"The party at seating S204 has not left yet: clear comes after leave",\
            "instance":"/seatings/S204/clear"}""";
                public static final String ALREADY_CLEARED = """
            {"type":"/errors/already-cleared","title":"Already cleared","status":409,\
            "detail":"Seating S204 has already been cleared","instance":"/seatings/S204/clear"}""";
                public static final String NO_SUCH_SEATING = """
            {"type":"/errors/no-such-seating","title":"No such seating","status":404,\
            "detail":"There is no seating S999","instance":"/seatings/S999/leave"}""";
    
    private ProblemExamples() {
    }
}
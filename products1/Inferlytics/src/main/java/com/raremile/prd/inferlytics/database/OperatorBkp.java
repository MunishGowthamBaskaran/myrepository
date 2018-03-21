package com.raremile.prd.inferlytics.database;


public class OperatorBkp {/*
						 * private static Logger LOG =
						 * Logger.getLogger(Operator.class);
						 * 
						 * private Connector con; private Connector rmconLex;
						 * private static OperatorBkp instance;
						 * 
						 * private OperatorBkp() { con = new
						 * Connector("rm_sentiment"); rmconLex = new
						 * Connector("rm_lexicondb"); }
						 */
	/**
	 * Singleton Operator instance getter.
	 * 
	 * This prevents multiple connections to the same database, and makes it
	 * easier to get a DB operator reference.
	 * 
	 * @return Operator instance.
	 */
	/*
	 * public static OperatorBkp getInstance() { if (instance == null) instance
	 * = new OperatorBkp(); assert (instance != null); return instance; }
	 * 
	 * public void storeFeedback(long id, SENTIMENTENUM sentiment) { try {
	 *//**
	 * Might be Add or Update
	 */
	/*
	 * LOG.info("ID is " + id); LOG.info("enum is " + sentiment.toString());
	 * 
	 * String insertStoreProc = "{call insert_update_feedback(?,?)}";
	 * PreparedStatement stmt = (PreparedStatement) con.getCon()
	 * .prepareCall(insertStoreProc); stmt.clearParameters(); stmt.setLong(1,
	 * id); stmt.setString(2, sentiment.toString());
	 * 
	 * stmt.executeUpdate();
	 * 
	 * LOG.info("After Insert OR UPDATE");
	 * 
	 * } catch (SQLException e) { // TODO: handle exception LOG.error("", e); }
	 * }
	 * 
	 * public void updateWord(List<String> words, DetailedSentiment sentiment) {
	 * LOG.info("Here updateWord" + words.toString() + "sentiment as " +
	 * sentiment); String query = ""; // if(sentiment == 0) query =
	 * "UPDATE setiwordnet_data SET " + sentiment.toString() + "=" +
	 * sentiment.toString() + "+1 WHERE word=? "; // else // query = //
	 * "UPDATE setiwordnet_data SET BadCount=BadCount+1 WHERE word=? ";
	 * PreparedStatement ps = null;
	 * 
	 * try { ps = (PreparedStatement) rmconLex.getCon().prepareStatement(query);
	 * 
	 * for (String word : words) { ps.setString(1, word); ps.addBatch(); }
	 * ps.executeBatch(); rmconLex.getCon().commit();
	 * 
	 * } catch (SQLException se) { LOG.error("", se); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } } }
	 * 
	 * public void updateNegation(List<String> negations, DetailedSentiment
	 * sentiment) { LOG.info("Here updateNegation" + negations.toString() +
	 * "sentiment as " + sentiment); String query = ""; // if(sentiment == 0)
	 * query = "UPDATE negations SET " + sentiment.toString() + "=" +
	 * sentiment.toString() + "+1 WHERE negation=? "; // else // query =
	 * "UPDATE negations SET BadCount=BadCount+1 WHERE negation=? ";
	 * PreparedStatement ps = null;
	 * 
	 * try { ps = (PreparedStatement) rmconLex.getCon().prepareStatement(query);
	 * 
	 * for (String negation : negations) { ps.setString(1, negation);
	 * ps.addBatch(); } ps.executeBatch(); rmconLex.getCon().commit();
	 * 
	 * } catch (SQLException se) { LOG.error("", se); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } } }
	 * 
	 * public void updateModifier(List<String> modifiers, DetailedSentiment
	 * sentiment) { LOG.info("Here updateModifier" + modifiers.toString() +
	 * "sentiment as " + sentiment); String query = ""; // if(sentiment == 0)
	 * query = "UPDATE modifiers SET " + sentiment.toString() + "=" +
	 * sentiment.toString() + "+1 WHERE modifier=? "; // else // query =
	 * "UPDATE modifiers SET BadCount=BadCount+1 WHERE modifier=? ";
	 * PreparedStatement ps = null;
	 * 
	 * try { ps = (PreparedStatement) rmconLex.getCon().prepareStatement(query);
	 * 
	 * for (String modifier : modifiers) { ps.setString(1, modifier);
	 * ps.addBatch(); } ps.executeBatch(); rmconLex.getCon().commit();
	 * 
	 * } catch (SQLException se) { LOG.error("", se); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } } }
	 *//**
	 * It stores a sentiment (tweet_id,entity_id,sentiment,score) //TODO
	 * CHECK LATER FUNCTIONALITY WHEN THE OTHERS PRODUCE DATA
	 */
	/*
	 * public void storeSentiment(long tweet_id, long entity_id, double
	 * sentiment, double score) { PreparedStatement ps = null; try { String
	 * query =
	 * "INSERT INTO sentiments(tweet_id, entity_id, score) VALUES(?,?,?)";
	 * 
	 * // Get a preparedStatement object ps = (PreparedStatement)
	 * con.getCon().prepareStatement(query);
	 * 
	 * // clear any previous parameter values ps.clearParameters(); // Insert
	 * some values into the table ps.setLong(1, tweet_id); ps.setLong(2,
	 * entity_id); ps.setDouble(3, score); ps.executeUpdate(); } catch
	 * (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally { try { if
	 * (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * //closeCon(); } }
	 *//**
	 * Wrote this method for using in getFeedsForEntity but moving this as
	 * not needed for feedback
	 * 
	 * @param feedId
	 * @return
	 */
	/*
	 * public ArrayList<Integer> getWordIdsForFeed(long feedId) {
	 * ArrayList<Integer> wpdIdList = new ArrayList<Integer>();
	 * PreparedStatement ps = null; ResultSet rs = null; String query =
	 * "SELECT WORD_ID FROM feedback WHERE POST_ID=?"; try { ps =
	 * rmconLex.getCon().prepareStatement(query); ps.setLong(1, feedId);
	 * 
	 * rs = ps.executeQuery(); while (rs.next()) { wpdIdList.add(rs.getInt(1));
	 * } } catch (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * }
	 * 
	 * return wpdIdList;
	 * 
	 * }
	 *//**
	 * It retrieves a tweet based on a keyword from database It gets the
	 * tweet which the key exists inside the content column
	 * 
	 * @param con
	 * @param key
	 *            : The keyword can be a general keyword e.g key= +greece -apple
	 * @return LinkedList with retrieved tweets
	 */
	/*
	 * public LinkedList<String> getTweets(String key) { LinkedList<String> list
	 * = new LinkedList<String>(); PreparedStatement ps = null; ResultSet rs =
	 * null; try { String query = "SELECT content FROM tweets " +
	 * "WHERE MATCH (content)  AGAINST ('" + key + "' IN BOOLEAN MODE);";
	 * con.getCon().setAutoCommit(false); ps = (PreparedStatement)
	 * con.getCon().prepareStatement(query); rs = (ResultSet) ps.executeQuery();
	 * while (rs.next()) {
	 * 
	 * list.add(rs.getString(1));
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } //closeCon();
	 * } return list; } public long getIdOfLatestTweetForThisEntity(String
	 * entity) { long result = 0; PreparedStatement ps = null; ResultSet rs =
	 * null; try { String query =
	 * "SELECT MAX(tweet_id) FROM sentiments WHERE entity_id = ?"; ps =
	 * (PreparedStatement) con.getCon().prepareStatement(query); long entityId =
	 * 0L;//getEntityId(entity); ps.setLong(1, entityId); rs =
	 * ps.executeQuery(); if (rs.next()) { result = rs.getLong(1); } } catch
	 * (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally { try { if
	 * (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } //closeCon();
	 * } return result; } public HashMap<String, Double>
	 * getLexiconEntries(String key) {
	 * 
	 * HashMap<String, Double> list = new HashMap<String, Double>();
	 * PreparedStatement ps = null; ResultSet rs = null; try { String query =
	 * "SELECT word,sentiment FROM entity_relatedword " +
	 * "WHERE MATCH (word)  AGAINST ('" + key + "' IN BOOLEAN MODE);";
	 * con.getCon().setAutoCommit(false); ps = (PreparedStatement)
	 * con.getCon().prepareStatement(query); rs = (ResultSet) ps.executeQuery();
	 * while (rs.next()) {
	 * 
	 * list.put(rs.getString(1), rs.getDouble(2));
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("", ex); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } //closeCon();
	 * } return list;
	 * 
	 * }
	 *//**
	 * It returns a list of related words with the entity
	 * 
	 * @param key_entity
	 * @return a List of the related words and the relational score
	 */
	/*
	 * public HashMap<String, Double> getRelatedWord(String key_entity) {
	 * 
	 * HashMap<String, Double> list = new HashMap<String, Double>();
	 * PreparedStatement ps = null; ResultSet rs = null; try { String query =
	 * "SELECT related_word,relation_score FROM entity_relatedword " +
	 * "WHERE MATCH (entity)  AGAINST ('" + key_entity + "' IN BOOLEAN MODE);";
	 * con.getCon().setAutoCommit(false); ps = (PreparedStatement)
	 * con.getCon().prepareStatement(query); rs = (ResultSet) ps.executeQuery();
	 * while (rs.next()) {
	 * 
	 * list.put(rs.getString(1), rs.getDouble(2));
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("", ex); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } //closeCon();
	 * } return list;
	 * 
	 * }
	 *//**
	 * It stores an entity and all the related words
	 * 
	 * @param entity
	 * @param related_word
	 * @param score
	 */
	/*
	 * public void storeEntityRelatedWord(String entity, String related_word,
	 * double score) { PreparedStatement ps = null; try { String query =
	 * "INSERT INTO entity_relatedword(entity, related_word, score) VALUES(?,?,?)"
	 * ;
	 * 
	 * // Get a preparedStatement object ps = (PreparedStatement)
	 * con.getCon().prepareStatement(query);
	 * 
	 * // clear any previous parameter values ps.clearParameters(); // Insert
	 * some values into the table ps.setString(1, entity); ps.setString(2,
	 * related_word); ps.setDouble(3, score); ps.executeUpdate(); } catch
	 * (SQLException ex) { LOG.error("", ex); } finally { try { if (ps != null)
	 * { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * //closeCon(); }
	 * 
	 * }
	 *//**
	 * It stores an entity(id,value,score) //TODO CHECK again functionality
	 * and format
	 */
	/*
	 * public void storeEntity(String value, double score) {
	 * 
	 * String query = "INSERT INTO entity(value,score) VALUES(?,?)";
	 * 
	 * // Get a preparedStatement object PreparedStatement ps = null; try { ps =
	 * (PreparedStatement) con.getCon().prepareStatement(query);
	 * 
	 * // clear any previous parameter values ps.clearParameters(); // Insert
	 * some values into the table ps.setString(1, value); ps.setDouble(2,
	 * score); ps.executeUpdate();
	 * 
	 * } catch (SQLException ex) { LOG.error("", ex); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * //closeCon(); }
	 * 
	 * }
	 *//**
	 * It retrieves a sentiment based on a keyword(tweet id)
	 * 
	 * @param con
	 * @param key
	 *            tweet_id
	 * @return LinkedList with sentiments (not sure about the format, check it
	 *         later) //TODO CHECK LATER FUNCTIONALITY WHEN THE OTHERS PRODUCE
	 *         DATA
	 */
	/*
	 * public double getSentiment(long key) { double score = 0.0;
	 * PreparedStatement ps = null; ResultSet rs = null; try { String query =
	 * "SELECT score FROM sentiments " + "WHERE tweet_id = ?";
	 * 
	 * ps = (PreparedStatement) con.getCon().prepareStatement(query);
	 * 
	 * ps.setLong(1, key);
	 * 
	 * rs = (ResultSet) ps.executeQuery(); while (rs.next()) {
	 * 
	 * score = rs.getDouble(1);
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("", ex); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } //closeCon();
	 * } return score; }
	 */
	/*
	 * private static final Logger LOG = Logger.getLogger(Operator.class); //
	 * Connection object with the database
	 * 
	 * private final Connector rmconLex; private static Operator instance;
	 * 
	 * private Operator() { rmconLex = new Connector("rm_lexicondb"); }
	 *//**
	 * Singleton Operator instance getter.
	 * 
	 * This prevents multiple connections to the same database, and makes it
	 * easier to get a DB operator reference.
	 * 
	 * @return Operator instance.
	 */
	/*
	 * public static Operator getInstance() { if (instance == null) { instance =
	 * new Operator(); } assert (instance != null); return instance; }
	 *//**
	 * Get the entityId from the database. If the entity is missing insert it
	 * and return the new id
	 * 
	 * @return
	 */
	/*
	 * public long getEntityId(String entity) { long entityId = 0;
	 * PreparedStatement ps = null; ResultSet rs = null; try { String query =
	 * "SELECT id FROM entity " + "WHERE value = ?";
	 * 
	 * ps = rmconLex.getCon().prepareStatement(query);
	 * 
	 * ps.setString(1, entity);
	 * 
	 * rs = ps.executeQuery(); // if we've had this entity before get its id and
	 * see if we've saved // tweets before. otherwise insert it so we don't have
	 * problems // further on if (rs.next()) { entityId = rs.getLong(1);
	 * LOG.info("The entity id is: " + entityId); } else { LOG.info("N-are");
	 * query = "INSERT INTO entity(value, score) VALUES(?, ?)"; ps =
	 * rmconLex.getCon().prepareStatement(query,
	 * Statement.RETURN_GENERATED_KEYS); ps.setString(1, entity);
	 * ps.setDouble(2, 0); ps.executeUpdate(); rs = ps.getGeneratedKeys(); //
	 * con.getCon().commit(); if (rs.next()) { entityId = rs.getLong(1);
	 * LOG.info("The entity id is: " + entityId); } else {
	 * LOG.info("Something screwey"); } } } catch (SQLException ex) {
	 * LOG.error("", ex); } finally { try { if (ps != null) { ps.close(); } }
	 * catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); } return entityId; }
	 *//**
	 * Retrieve the entire lexicon from the database.
	 * 
	 * These are key-value pairs of words and their sentimental value.
	 */
	/*
	 * public HashMap<String, Double> getLexicon() throws SQLException {
	 * HashMap<String, Double> lexicon = new HashMap<String, Double>(); String
	 * query = "SELECT `word`, `sentiment` FROM `setiwordnet_data`";// where //
	 * sentiment // > // 0.35 // or // sentiment<-0.35"; PreparedStatement ps =
	 * null; ResultSet rs = null; try { ps =
	 * rmconLex.getCon().prepareStatement(query); rs = ps.executeQuery();
	 * 
	 * while (rs.next()) { lexicon.put(rs.getString(1), rs.getDouble(2)); }
	 * 
	 * LOG.info("SIZE of LEXiCON " + lexicon.size()); } catch (SQLException e) {
	 * LOG.error("SQLException", e); } finally { try { if (ps != null) {
	 * ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); }
	 * 
	 * return lexicon; }
	 *//**
	 * Retrieve the entire word pattern from the database. joining with
	 * MST_patterns
	 * 
	 */
	/*
	 * public HashMap<String, List<WordPatternScore>> getWordPatternScore()
	 * throws SQLException { HashMap<String, List<WordPatternScore>> wpsList =
	 * new HashMap<String, List<WordPatternScore>>(); String query =
	 * "SELECT  wps.ID AS id, WORD AS word , patterns.PATTERN AS pattern ,`SCORE` AS score "
	 * +
	 * "FROM word_pattern_score_21mar13 wps,mst_paterns_21mar13 patterns WHERE wps.PATTERN_ID = patterns.ID AND wps.SCORE!=0"
	 * ; PreparedStatement ps = null; ResultSet rs = null;
	 * 
	 * try {
	 * 
	 * ps = rmconLex.getCon().prepareStatement(query); rs = ps.executeQuery();
	 * 
	 * while (rs.next()) { WordPatternScore wps = new WordPatternScore(); String
	 * word = rs.getString(2).split("#")[0]; wps.setId(rs.getInt(1));
	 * wps.setWord(rs.getString(2)); wps.setPattern(rs.getString(3));
	 * wps.setScore(rs.getDouble(4));
	 * 
	 * if (null != word && !word.isEmpty()) { List<WordPatternScore> list =
	 * wpsList.get(word); if (null == list) { list = new
	 * ArrayList<WordPatternScore>(); } list.add(wps); wpsList.put(word, list);
	 * }
	 * 
	 * } } catch (SQLException e) { LOG.error("", e); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); } return wpsList; }
	 *//**
	 * It stores an entity(id,value,score) //TODO CHECK again functionality
	 * and format TODO change it to batch if this is needed for inserting wps
	 * from sentiword file. Now removing batch as IDs are needed for inserting
	 * into feedback table.
	 */
	/*
	 * public ArrayList<Integer> storeWordPattern(List<WordPatternScore> list) {
	 * 
	 * ArrayList<Integer> wpsIdList = new ArrayList<Integer>();
	 * 
	 * String insertPattern =
	 * "  INSERT   INTO `mst_paterns_21mar13` (`PATTERN`) VALUES (?) ON DUPLICATE KEY UPDATE `ID`=LAST_INSERT_ID(`ID`)"
	 * ; String insertWPS =
	 * "INSERT  INTO `word_pattern_score_21mar13` (`WORD`,`PATTERN_ID`,`SCORE`) VALUES(?,?,?) ON DUPLICATE KEY UPDATE `ID`=LAST_INSERT_ID(`ID`)"
	 * ; PreparedStatement psForWPS = null; PreparedStatement psForPattern =
	 * null; ResultSet rsForPattern = null; ResultSet rsForWPS = null;
	 * 
	 * try {
	 * 
	 * // Get a preparedStatement object psForWPS =
	 * rmconLex.getCon().prepareStatement( insertWPS,
	 * Statement.RETURN_GENERATED_KEYS); psForPattern = rmconLex.getCon()
	 * .prepareStatement(insertPattern, Statement.RETURN_GENERATED_KEYS);
	 * 
	 * // psForWPS.clearParameters(); // psForPattern.clearParameters();
	 * 
	 * for (WordPatternScore wps : list) {
	 * 
	 * psForWPS.setString(1, wps.getWord()); psForPattern.setString(1,
	 * wps.getPattern()); // psForPattern.setString(2, wps.getPattern());
	 * psForPattern.executeUpdate(); rsForPattern =
	 * psForPattern.getGeneratedKeys(); if (rsForPattern.next()) { int newId =
	 * rsForPattern.getInt(1); psForWPS.setInt(2, newId); } else {
	 * LOG.error("NOT getting id for this pattern " + wps.getPattern()); }
	 * psForWPS.setDouble(3, wps.getScore()); // psForWPS.setString(4,
	 * wps.getWord());
	 * 
	 * psForWPS.executeUpdate(); rsForWPS = psForWPS.getGeneratedKeys(); if
	 * (rsForWPS.next()) { int newId = rsForWPS.getInt(1);
	 * LOG.info("ID after insert or update od wordpattern " + newId +
	 * "for the word " + wps.getWord()); wpsIdList.add(newId); } }
	 * 
	 * } catch (SQLException ex) { LOG.error("", ex); } finally { try { if
	 * (psForPattern != null) { psForPattern.close(); } if (psForWPS != null) {
	 * psForWPS.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if
	 * (rsForPattern != null) { rsForPattern.close(); } if (rsForWPS != null) {
	 * rsForWPS.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * closeRMConLex(); } wpsIdList.trimToSize(); return wpsIdList;
	 * 
	 * }
	 * 
	 * public void storeWordPatternAsBatch(List<WordPatternScore> list) {
	 * 
	 * String insertPattern =
	 * "  INSERT  INTO `mst_paterns_21mar13` (`PATTERN`) VALUES (?) ON DUPLICATE KEY UPDATE `ID`=LAST_INSERT_ID(ID)"
	 * ; String query =
	 * "INSERT INTO `word_pattern_score_21mar13` (WORD,PATTERN_ID,SCORE) VALUES(?,?,?)"
	 * ; PreparedStatement ps = null; PreparedStatement psForPattern = null; try
	 * {
	 * 
	 * // Get a preparedStatement object ps =
	 * rmconLex.getCon().prepareStatement(query); psForPattern =
	 * rmconLex.getCon() .prepareStatement(insertPattern,
	 * Statement.RETURN_GENERATED_KEYS);
	 * 
	 * int i = 0; for (WordPatternScore wps : list) {
	 * 
	 * ps.setString(1, wps.getWord()); psForPattern.setString(1,
	 * wps.getPattern()); // psForPattern.setString(2, wps.getPattern());
	 * psForPattern.executeUpdate(); ResultSet rs =
	 * psForPattern.getGeneratedKeys(); if (rs.next()) { int newId =
	 * rs.getInt(1); ps.setInt(2, newId); LOG.info("word " + wps.getWord() +
	 * " pattern " + newId + " " + wps.getPattern() + " score " +
	 * wps.getScore()); } ps.setDouble(3, wps.getScore());
	 * 
	 * ps.addBatch(); i++;
	 * 
	 * if (i == 1000) { ps.executeBatch(); i = 0; } }
	 * 
	 * ps.executeBatch(); } catch (SQLException ex) { LOG.error("", ex); }
	 * finally { try { if (ps != null) { ps.close(); } } catch (SQLException ex)
	 * { LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * closeRMConLex(); } }
	 *//**
	 * One time job
	 * 
	 * @param lexicon
	 */
	/*
	 * public void storeLexicon(Map<String, Double> lexicon) { PreparedStatement
	 * ps = null; try { String query =
	 * "INSERT INTO setiwordnet_data(word,sentiment) VALUES(?,?)";
	 * 
	 * // Get a preparedStatement object ps =
	 * rmconLex.getCon().prepareStatement(query); // clear any previous
	 * parameter values // stmt.clearParameters(); // Insert some values into
	 * the table int i = 0; for (String key : lexicon.keySet()) {
	 * ps.setString(1, key); ps.setDouble(2, lexicon.get(key));
	 * 
	 * ps.addBatch(); i++;
	 * 
	 * if (i == 1000) { ps.executeBatch(); i = 0; } }
	 * 
	 * ps.executeBatch(); } catch (SQLException ex) { LOG.error("", ex); }
	 * finally { try { if (ps != null) { ps.close(); } } catch (SQLException ex)
	 * { LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * closeRMConLex(); }
	 * 
	 * }
	 *//**
	 * Retrieve the entire stopword from the database.
	 * 
	 * This is a set to avoid duplicate entries
	 * 
	 * @throws SQLException
	 */
	/*
	 * public Set<String> getStopword() throws SQLException { Set<String>
	 * stopwords = new HashSet<String>(); String query =
	 * "SELECT `word` FROM `stopwords`"; PreparedStatement ps = null; try { ps =
	 * rmconLex.getCon().prepareStatement(query); ResultSet result =
	 * ps.executeQuery();
	 * 
	 * while (result.next()) { stopwords.add(result.getString(1)); } } catch
	 * (SQLException e) { LOG.error("SQL EXCEPTION", e); } finally { try { if
	 * (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * closeRMConLex(); } return stopwords;
	 * 
	 * }
	 *//**
	 * Closes the connection with sentimentDB
	 */
	/*
	 * public void closeCon() { // TODO c0mmment this // con.closeCon(); }
	 *//**
	 * Closes the connection with the RMlexiconDB
	 */
	/*
	 * public void closeRMConLex() {
	 * 
	 * rmconLex.closeCon(); }
	 *//**
	 * 
	 * @return smiley list
	 */
	/*
	 * public Set<String> getSmileysList() { Set<String> smileys = new
	 * HashSet<String>(); PreparedStatement ps = null; ResultSet rs = null; try
	 * { String query = "SELECT smiley FROM smileys;";
	 * rmconLex.getCon().setAutoCommit(false); ps =
	 * rmconLex.getCon().prepareStatement(query); rs = ps.executeQuery(); while
	 * (rs.next()) { smileys.add(rs.getString(1)); }
	 * 
	 * } catch (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); } return smileys; }
	 *//**
	 * @return Smileys and corresponding score.
	 */
	/*
	 * public HashMap<String, Double> getSmileys() { HashMap<String, Double>
	 * list = new HashMap<String, Double>(); PreparedStatement ps = null;
	 * ResultSet rs = null; try { String query =
	 * "SELECT smiley,value FROM smileys;";
	 * rmconLex.getCon().setAutoCommit(false); ps =
	 * rmconLex.getCon().prepareStatement(query); rs = ps.executeQuery(); while
	 * (rs.next()) {
	 * 
	 * list.put(rs.getString(1), rs.getDouble(2));
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); } return list; }
	 *//**
	 * @return Modifier and corresponding score.
	 */
	/*
	 * public HashMap<String, Double> getModifiers() { HashMap<String, Double>
	 * list = new HashMap<String, Double>(); PreparedStatement ps = null;
	 * ResultSet rs = null; try { String query =
	 * "SELECT modifier,value FROM modifiers;";
	 * rmconLex.getCon().setAutoCommit(false); ps =
	 * rmconLex.getCon().prepareStatement(query); rs = ps.executeQuery(); while
	 * (rs.next()) {
	 * 
	 * list.put(rs.getString(1), rs.getDouble(2));
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); } return list; }
	 *//**
	 * @return Negation and corresponding score.
	 */
	/*
	 * public HashMap<String, Double> getNegations() { HashMap<String, Double>
	 * list = new HashMap<String, Double>(); PreparedStatement ps = null;
	 * ResultSet rs = null; try { String query =
	 * "SELECT negation,value FROM negations;";
	 * rmconLex.getCon().setAutoCommit(false); ps =
	 * rmconLex.getCon().prepareStatement(query); rs = ps.executeQuery(); while
	 * (rs.next()) {
	 * 
	 * list.put(rs.getString(1), rs.getDouble(2));
	 * 
	 * }
	 * 
	 * } catch (SQLException ex) { LOG.error("SQLEXCEPTION ", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * closeRMConLex(); } return list; }
	 *//**
	 * Method used while creating POS patterns . A one time use.
	 * 
	 * @param list
	 */
	/*
	 * 
	 * public void storeSentiword(List<Sentiword> list) { String query =
	 * "INSERT IGNORE INTO  `sentiword_pattern`" +
	 * "             (POS,ID,POSScore,NegScore,word,Glossary,pattern) " +
	 * "VALUES       (?,?,?,?,?,?,?)        "; PreparedStatement ps = null; try
	 * { int count = 0; ps = rmconLex.getCon().prepareStatement(query); for
	 * (Sentiword senti : list) { ++count; ps.setString(1, senti.getPOS());
	 * ps.setLong(2, senti.getId()); ps.setDouble(3, senti.getPosscore());
	 * ps.setDouble(4, senti.getNegscore()); ps.setString(5, senti.getWord());
	 * ps.setString(6, senti.getGlossary()); ps.setString(7,
	 * senti.getPattern());
	 * 
	 * ps.addBatch(); if (count % 1000 == 0) { count = 0; ps.executeBatch(); } }
	 * ps.executeBatch(); } catch (SQLException sqlex) { System.out
	 * .println("It's not saving because the following error occurs ");
	 * sqlex.printStackTrace(); } finally { try { if (ps != null) { ps.close();
	 * } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * // closeRMConLex(); } }
	 *//**
	 * 
	 * @param negation
	 */
	/*
	 * public void storeNegation(String negation) {
	 * 
	 * String query = "insert into negations(negation) values(?)";
	 * PreparedStatement ps = null; try { ps =
	 * rmconLex.getCon().prepareStatement(query); ps.setString(1, negation);
	 * 
	 * ps.executeUpdate(); } catch (SQLException e) { LOG.error("SQL Exception",
	 * e); } finally { try { if (ps != null) { ps.close(); } } catch
	 * (SQLException ex) { LOG.error("Error While Closing Prepared Statement ",
	 * ex); }
	 * 
	 * closeRMConLex(); } }
	 *//**
	 * 
	 * @param modifier
	 * @param score
	 */
	/*
	 * public void storeModifier(String modifier, Double score) {
	 * 
	 * String query = "insert into modifiers(modifier,value) values(?,?)";
	 * PreparedStatement ps = null; try { ps =
	 * rmconLex.getCon().prepareStatement(query); ps.setString(1, modifier);
	 * ps.setDouble(2, score);
	 * 
	 * ps.executeUpdate(); } catch (SQLException e) { LOG.error("SQL Exception",
	 * e); } finally { try { if (ps != null) { ps.close(); } } catch
	 * (SQLException ex) { LOG.error("Error While Closing Prepared Statement ",
	 * ex); }
	 * 
	 * closeRMConLex(); } }
	 *//**
	 * This is the method which takes user feedback. The method accepts
	 * postId and sentiment and updates the feedback table with the given
	 * sentiment.
	 * 
	 * @param sentiment
	 * @param postId
	 */
	/*
	 * public void updatePOSPattern(DetailedSentiment sentiment, long postId) {
	 * LOG.info("Here updatePOSPattern" + postId + "sentiment as " + sentiment);
	 * String query = "";
	 * 
	 * query = "UPDATE feedback SET " + sentiment.toString() + "=" +
	 * sentiment.toString() + "+1 WHERE  POST_ID=?";
	 * 
	 * PreparedStatement ps = null;
	 * 
	 * try { ps = rmconLex.getCon().prepareStatement(query); ps.setLong(1,
	 * postId); ps.executeUpdate(); // rmconLex.getCon().commit();
	 * 
	 * } catch (SQLException se) { LOG.error("", se); } finally { try { if (ps
	 * != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } } }
	 *//**
	 * 
	 * @param word
	 * @return list of synonymns
	 */
	/*
	 * public ArrayList<String> getSynonymsByWords(String word) {
	 * ArrayList<String> synonyms = new ArrayList<String>(); String query =
	 * "SELECT Word FROM synonyms WHERE SynonymId IN( SELECT SynonymId FROM synonyms WHERE Word = ?)"
	 * ; PreparedStatement ps = null; ResultSet rs = null; try { ps =
	 * rmconLex.getCon().prepareStatement(query); ps.setString(1, word); rs =
	 * ps.executeQuery(); while (rs.next()) {
	 * 
	 * synonyms.add(rs.getString(1));
	 * 
	 * } } catch (SQLException ex) {
	 * LOG.error("SQL Exception in getSynonymsByWords method", ex); } finally {
	 * try { if (ps != null) { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } try { if (rs
	 * != null) { rs.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Result Set ", ex); } } return synonyms;
	 * 
	 * }
	 *//**
	 * This is a method used by batch job for updating Word Pattern score
	 * based on user feedback
	 * 
	 * This method accepts the input parameter as query
	 * 
	 * @param query
	 */
	/*
	 * public void updateWordScoreJob(String query) { PreparedStatement ps =
	 * null; try { ps = rmconLex.getCon().prepareStatement(query); } catch
	 * (SQLException se) { LOG.error("", se); } finally { try { if (ps != null)
	 * { ps.close(); } } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); } }
	 * 
	 * }
	 * 
	 * public static void main(String args[]) { String insertPattern =
	 * "  INSERT IGNORE  INTO `mst_paterns_temp` (`PATTERN`) VALUES (?) ON DUPLICATE KEY UPDATE `ID`=LAST_INSERT_ID(ID)"
	 * ; PreparedStatement ps = null; try { ps =
	 * Operator.getInstance().rmconLex.getCon() .prepareStatement(insertPattern,
	 * Statement.RETURN_GENERATED_KEYS); ps.setString(1, " CC RB RB JJ"); //
	 * ps.setString(2, " JJ CC JJ"); ps.executeUpdate(); ResultSet rs =
	 * ps.getGeneratedKeys(); if (rs.next()) { int newId = rs.getInt(1);
	 * LOG.info("adasdasdsa" + newId); } else { LOG.info("same prob"); } } catch
	 * (SQLException ex) { LOG.error("", ex); } finally { try { if (ps != null)
	 * { ps.close(); }
	 * 
	 * } catch (SQLException ex) {
	 * LOG.error("Error While Closing Prepared Statement ", ex); }
	 * 
	 * // closeRMConLex(); } }
	 */
}

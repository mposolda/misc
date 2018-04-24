package org.mposolda.ispn.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.infinispan.commons.marshall.Externalizer;
import org.infinispan.commons.marshall.MarshallUtil;
import org.infinispan.commons.marshall.SerializeWith;
import org.jboss.logging.Logger;
import org.mposolda.ispn.v2.KeycloakMarshallUtil;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@SerializeWith(UserSessionEntity.ExternalizerImpl.class)
public class UserSessionEntity implements Serializable /*extends SessionEntity*/ {

    public static final Logger logger = Logger.getLogger(UserSessionEntity.class);

    // Metadata attribute, which contains the lastSessionRefresh available on remoteCache. Used in decide whether we need to write to remoteCache (DC) or not
    public static final String LAST_SESSION_REFRESH_REMOTE = "lsrr";

    private String id;

    private String realmId;

    private String user;

    private String brokerSessionId;
    private String brokerUserId;

    private String loginUsername;

    private String ipAddress;

    private String authMethod;

    private boolean rememberMe;

    private int started;

    // CHANGED FROM int TO String in new version
    private String lastSessionRefresh;

    //private UserSessionModel.State state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    private Map<String, String> notes = new ConcurrentHashMap<>();

    //private AuthenticatedClientSessionStore authenticatedClientSessions = new AuthenticatedClientSessionStore();

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(String authMethod) {
        this.authMethod = authMethod;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }

    public String getLastSessionRefresh() {
        return lastSessionRefresh;
    }

    public void setLastSessionRefresh(String lastSessionRefresh) {
        this.lastSessionRefresh = lastSessionRefresh;
    }

    public Map<String, String> getNotes() {
        return notes;
    }

    public void setNotes(Map<String, String> notes) {
        this.notes = notes;
    }
//
//    public AuthenticatedClientSessionStore getAuthenticatedClientSessions() {
//        return authenticatedClientSessions;
//    }
//
//    public void setAuthenticatedClientSessions(AuthenticatedClientSessionStore authenticatedClientSessions) {
//        this.authenticatedClientSessions = authenticatedClientSessions;
//    }
//
//    public UserSessionModel.State getState() {
//        return state;
//    }
//
//    public void setState(UserSessionModel.State state) {
//        this.state = state;
//    }

    public String getBrokerSessionId() {
        return brokerSessionId;
    }

    public void setBrokerSessionId(String brokerSessionId) {
        this.brokerSessionId = brokerSessionId;
    }

    public String getBrokerUserId() {
        return brokerUserId;
    }

    public void setBrokerUserId(String brokerUserId) {
        this.brokerUserId = brokerUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSessionEntity)) return false;

        UserSessionEntity that = (UserSessionEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("UserSessionEntity [id=%s, realm=%s, started=%d, lastSessionRefresh=%s ]", getId(), getRealmId(),
                getStarted(), getLastSessionRefresh());
    }

//    @Override
//    public SessionEntityWrapper mergeRemoteEntityWithLocalEntity(SessionEntityWrapper localEntityWrapper) {
//        int lsrRemote = getLastSessionRefresh();
//
//        SessionEntityWrapper entityWrapper;
//        if (localEntityWrapper == null) {
//            entityWrapper = new SessionEntityWrapper<>(this);
//        } else {
//            UserSessionEntity localUserSession = (UserSessionEntity) localEntityWrapper.getEntity();
//
//            // local lastSessionRefresh should always contain the bigger
//            if (lsrRemote < localUserSession.getLastSessionRefresh()) {
//                setLastSessionRefresh(localUserSession.getLastSessionRefresh());
//            }
//
//            entityWrapper = new SessionEntityWrapper<>(localEntityWrapper.getLocalMetadata(), this);
//        }
//
//        entityWrapper.putLocalMetadataNoteInt(LAST_SESSION_REFRESH_REMOTE, lsrRemote);
//
//        logger.debugf("Updating session entity '%s'. lastSessionRefresh=%d, lastSessionRefreshRemote=%d", getId(), getLastSessionRefresh(), lsrRemote);
//
//        return entityWrapper;
//    }


    public static class ExternalizerImpl implements Externalizer<UserSessionEntity> {

        private static final int VERSION_1 = 1;
        private static final int VERSION_2 = 2;

//        private static final EnumMap<UserSessionModel.State, Integer> STATE_TO_ID = new EnumMap<>(UserSessionModel.State.class);
//        private static final Map<Integer, UserSessionModel.State> ID_TO_STATE = new HashMap<>();
//        static {
//            STATE_TO_ID.put(State.LOGGED_IN, 1);
//            STATE_TO_ID.put(State.LOGGED_OUT, 2);
//            STATE_TO_ID.put(State.LOGGING_OUT, 3);
//
//            for (Map.Entry<State, Integer> entry : STATE_TO_ID.entrySet()) {
//                ID_TO_STATE.put(entry.getValue(), entry.getKey());
//            }
//        }

        @Override
        public void writeObject(ObjectOutput output, UserSessionEntity session) throws IOException {
            output.writeByte(VERSION_2);

            MarshallUtil.marshallString(session.getAuthMethod(), output);
            MarshallUtil.marshallString(session.getBrokerSessionId(), output);
            MarshallUtil.marshallString(session.getBrokerUserId(), output);
            MarshallUtil.marshallString(session.getId(), output);
            MarshallUtil.marshallString(session.getIpAddress(), output);
            MarshallUtil.marshallString(session.getLoginUsername(), output);
            MarshallUtil.marshallString(session.getRealmId(), output);
            MarshallUtil.marshallString(session.getUser(), output);

            MarshallUtil.marshallString(String.valueOf(session.getLastSessionRefresh()), output);
            output.writeInt(session.getStarted());
            output.writeBoolean(session.isRememberMe());

//            int state = session.getState() == null ? 0 : STATE_TO_ID.get(session.getState());
//            output.writeInt(state);

            Map<String, String> notes = session.getNotes();
            KeycloakMarshallUtil.writeMap(notes, KeycloakMarshallUtil.STRING_EXT, KeycloakMarshallUtil.STRING_EXT, output);

//            output.writeObject(session.getAuthenticatedClientSessions());
        }


        @Override
        public UserSessionEntity readObject(ObjectInput input) throws IOException, ClassNotFoundException {
            int version = input.readByte();
            switch (version) {
                case VERSION_1:
                case VERSION_2:
                    return readObject(input, version);
                default:
                    throw new IOException("Unknown version");
            }
        }

        private UserSessionEntity readObject(ObjectInput input, int version) throws IOException, ClassNotFoundException {
            UserSessionEntity sessionEntity = new UserSessionEntity();

            sessionEntity.setAuthMethod(MarshallUtil.unmarshallString(input));
            sessionEntity.setBrokerSessionId(MarshallUtil.unmarshallString(input));
            sessionEntity.setBrokerUserId(MarshallUtil.unmarshallString(input));
            final String userSessionId = MarshallUtil.unmarshallString(input);
            sessionEntity.setId(userSessionId);
            sessionEntity.setIpAddress(MarshallUtil.unmarshallString(input));
            sessionEntity.setLoginUsername(MarshallUtil.unmarshallString(input));
            sessionEntity.setRealmId(MarshallUtil.unmarshallString(input));
            sessionEntity.setUser(MarshallUtil.unmarshallString(input));

            if (version == 1) {
                // WAS int IN VERSION 1
                System.out.println("Unmarshall lastSessionRefresh int");
                int lsrInt = input.readInt();
                sessionEntity.setLastSessionRefresh(String.valueOf(lsrInt));
            } else {
                // STRING in VERSION 2
                System.out.println("Unmarshall lastSessionRefresh String");
                sessionEntity.setLastSessionRefresh(MarshallUtil.unmarshallString(input));
            }

            sessionEntity.setStarted(input.readInt());
            sessionEntity.setRememberMe(input.readBoolean());

//            sessionEntity.setState(ID_TO_STATE.get(input.readInt()));

            Map<String, String> notes = KeycloakMarshallUtil.readMap(input, KeycloakMarshallUtil.STRING_EXT, KeycloakMarshallUtil.STRING_EXT,
                    new KeycloakMarshallUtil.ConcurrentHashMapBuilder<>());
            sessionEntity.setNotes(notes);

//            AuthenticatedClientSessionStore authSessions = (AuthenticatedClientSessionStore) input.readObject();
//            sessionEntity.setAuthenticatedClientSessions(authSessions);

            return sessionEntity;
        }

    }



    public static class ExternalizerImpll implements Externalizer<UserSessionEntity> {

        private static final int VERSION_1 = 1;
        private static final int VERSION_2 = 2;
        private static final int VERSION_3 = 3;

        private static final Map<Integer, UserSessionReader> VERSION_TO_READER = new HashMap<>();
        static {
            VERSION_TO_READER.put(VERSION_1, new UserSessionReaderV1());
            VERSION_TO_READER.put(VERSION_2, new UserSessionReaderV2());
            //VERSION_TO_READER.put(VERSION_2, new UserSessionReaderV3());
        }

        private static final List<UserSessionMigrator> MIGRATORS = new LinkedList<>();
        static {
            MIGRATORS.add(new UserSessionMigratorToV2());
            MIGRATORS.add(new UserSessionMigratorToV3());
        }

//        private static final EnumMap<UserSessionModel.State, Integer> STATE_TO_ID = new EnumMap<>(UserSessionModel.State.class);
//        private static final Map<Integer, UserSessionModel.State> ID_TO_STATE = new HashMap<>();
//        static {
//            STATE_TO_ID.put(State.LOGGED_IN, 1);
//            STATE_TO_ID.put(State.LOGGED_OUT, 2);
//            STATE_TO_ID.put(State.LOGGING_OUT, 3);
//
//            for (Map.Entry<State, Integer> entry : STATE_TO_ID.entrySet()) {
//                ID_TO_STATE.put(entry.getValue(), entry.getKey());
//            }
//        }

        @Override
        public void writeObject(ObjectOutput output, UserSessionEntity session) throws IOException {
            output.writeByte(VERSION_3);

            MarshallUtil.marshallString(session.getAuthMethod(), output);
            MarshallUtil.marshallString(session.getBrokerSessionId(), output);
            MarshallUtil.marshallString(session.getBrokerUserId(), output);
            MarshallUtil.marshallString(session.getId(), output);
            MarshallUtil.marshallString(session.getIpAddress(), output);
            MarshallUtil.marshallString(session.getLoginUsername(), output);
            MarshallUtil.marshallString(session.getRealmId(), output);
            MarshallUtil.marshallString(session.getUser(), output);

            MarshallUtil.marshallString(String.valueOf(session.getLastSessionRefresh()), output);
            output.writeInt(session.getStarted());
            output.writeBoolean(session.isRememberMe());

//            int state = session.getState() == null ? 0 : STATE_TO_ID.get(session.getState());
//            output.writeInt(state);

            Map<String, String> notes = session.getNotes();
            KeycloakMarshallUtil.writeMap(notes, KeycloakMarshallUtil.STRING_EXT, KeycloakMarshallUtil.STRING_EXT, output);

//            output.writeObject(session.getAuthenticatedClientSessions());
        }


        @Override
        public UserSessionEntity readObject(ObjectInput input) throws IOException, ClassNotFoundException {
            int version = input.readByte();

            switch (version) {
                case VERSION_1:
                case VERSION_2:
                    // Get reader for my version
                    UserSessionReader entityReader = VERSION_TO_READER.get(version);

                    // Read de-typed entity of old version entity data
                    Map<String, Object> entityData = entityReader.readOldUserSession(input);

                    // Convert entity data through chain of migrators to newest version
                    for (UserSessionMigrator migrator : MIGRATORS) {
                        if (migrator.getVersion() > version) {
                            migrator.migrate(entityData);
                        }
                    }

                    UserSessionEntity userSession = convertToEntity(entityData);
                    return userSession;

                    // Newest version. Avoid detyped reads for performance purposes
                case VERSION_3:
                    return readObject(input, version);
                default:
                    throw new IOException("Unknown version");
            }
        }

        private UserSessionEntity readObject(ObjectInput input, int version) throws IOException, ClassNotFoundException {
            UserSessionEntity sessionEntity = new UserSessionEntity();

            sessionEntity.setAuthMethod(MarshallUtil.unmarshallString(input));
            sessionEntity.setBrokerSessionId(MarshallUtil.unmarshallString(input));
            sessionEntity.setBrokerUserId(MarshallUtil.unmarshallString(input));
            final String userSessionId = MarshallUtil.unmarshallString(input);
            sessionEntity.setId(userSessionId);
            sessionEntity.setIpAddress(MarshallUtil.unmarshallString(input));
            sessionEntity.setLoginUsername(MarshallUtil.unmarshallString(input));
            sessionEntity.setRealmId(MarshallUtil.unmarshallString(input));
            sessionEntity.setUser(MarshallUtil.unmarshallString(input));

            // STRING in VERSION 3
            System.out.println("Unmarshall lastSessionRefresh String");
            sessionEntity.setLastSessionRefresh(MarshallUtil.unmarshallString(input));

            sessionEntity.setStarted(input.readInt());
            sessionEntity.setRememberMe(input.readBoolean());

//            sessionEntity.setState(ID_TO_STATE.get(input.readInt()));

            Map<String, String> notes = KeycloakMarshallUtil.readMap(input, KeycloakMarshallUtil.STRING_EXT, KeycloakMarshallUtil.STRING_EXT,
                    new KeycloakMarshallUtil.ConcurrentHashMapBuilder<>());
            sessionEntity.setNotes(notes);

//            AuthenticatedClientSessionStore authSessions = (AuthenticatedClientSessionStore) input.readObject();
//            sessionEntity.setAuthenticatedClientSessions(authSessions);

            return sessionEntity;
        }

        private UserSessionEntity convertToEntity(Map<String, Object> data) {
            UserSessionEntity entity = new UserSessionEntity();
            entity.setId(data.get("id").toString());
            entity.setRealmId(data.get("realmId").toString());

            // Reads the String now
            entity.setLastSessionRefresh(data.get("lastSessionRefresh").toString());

            // TODO: rest...

            return entity;
        }

    }



    public interface UserSessionReader {

        Map<String, Object> readOldUserSession(ObjectInput input) throws IOException;

    }

    public interface UserSessionMigrator {

        /**
         * Return my version
         * @return
         */
        int getVersion();

        void migrate(Map<String, Object> previousVersionEntity);

    }

    // READERS

    private static class UserSessionReaderV1 implements UserSessionReader {

        @Override
        public Map<String, Object> readOldUserSession(ObjectInput input) throws IOException {
            Map<String, Object> data = new HashMap<>();

            String id = MarshallUtil.unmarshallString(input);
            data.put("id", id);

            String realmId = MarshallUtil.unmarshallString(input);
            data.put("realmId", realmId);

            // Read lastSessionRefresh as int
            Integer lastSessionRefresh = input.readInt();
            data.put("lastSessionRefresh", lastSessionRefresh);

            // TODO: rest...

            return data;
        }
    }


    private static class UserSessionReaderV2 implements UserSessionReader {

        @Override
        public Map<String, Object> readOldUserSession(ObjectInput input) throws IOException {
            Map<String, Object> data = new HashMap<>();

            String id = MarshallUtil.unmarshallString(input);
            data.put("id", id);

            String realmId = MarshallUtil.unmarshallString(input);
            data.put("realmId", realmId);

            // Read lastSessionRefresh as int
            Integer lastSessionRefresh = input.readInt();
            data.put("lastSessionRefresh", lastSessionRefresh);

            // TODO: rest...

            return data;
        }
    }

    // MIGRATORS

    private static class UserSessionMigratorToV2 implements UserSessionMigrator {

        @Override
        public int getVersion() {
            return ExternalizerImpll.VERSION_2;
        }

        @Override
        public void migrate(Map<String, Object> entityData) {
        }
    }

    private static class UserSessionMigratorToV3 implements UserSessionMigrator {

        @Override
        public int getVersion() {
            return ExternalizerImpll.VERSION_3;
        }

        @Override
        public void migrate(Map<String, Object> entityData) {
            Integer lastSessionRefresh = (Integer) entityData.get("lastSessionRefresh");

            String newLsr = String.valueOf(lastSessionRefresh);

            entityData.put("lastSessionRefresh", newLsr);
        }
    }



}
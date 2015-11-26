package com.innometrics.integrationapp;

import com.innometrics.integrationapp.appsettings.FieldsEntry;
import com.innometrics.integrationapp.appsettings.RulesEntry;
import com.innometrics.integrationapp.model.Attribute;
import com.innometrics.integrationapp.model.Event;
import com.innometrics.integrationapp.model.Profile;
import jdk.nashorn.internal.ir.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by killpack on 26.11.15.
 */
public class InnoTransformer {
    RulesEntry[] rulesEntries ;
    public static final String
            PROFILE_ATTRIBUTE = "profileAttribute",
            PROFILE_ID = "profileId",
            PROFILE_SIZE = "profileSize",
            EVENT_VALUE = "eventValue",
            SESSION_VALUE = "sessionValue",
            STATIC_VALUE = "static",
            MACRO_VALUE = "macro",
            META_VALUE = "meta";
     static final String
            MARCO_TIMESTAMP_NOW = "timestamp_now",
            MARCO_TIMESTAMP_EVENT = "timestamp_event",
            MARCO_REQUEST_IP = "request_ip",
            MARCO_USER_AGENT = "user_agent";
    private static final String
            META_PROFILE_ID = "profileId",
            META_COMPANY_ID = "company_id",
            META_BUCKET_ID = "bucket_id",
            META_EVENT_DEFINITION_ID = "event_def_id",
            META_APP_SECTION_EVENT = "app_section_event",
            META_COLLECT_APP = "collect_app",
            META_SECTION = "section";



//    private static String getValueRef(FieldsEntry field) throws CrossSystemMessageProcessingException {
//        if (field.getValueRef() == null) throw new CrossSystemMessageProcessingException(String.format("ValueRef is missing for field: %s", field.getFieldName()));
//        return field.getValueRef().asText();
//    }
//
//    private static JsonNode getUserAttribute(List<Attribute> attributes, String valueLocation) {
//        String[] layers = valueLocation.split("/");
//        for (Attribute attribute : attributes) {
//            if (attribute.getCollectApp().equals(layers[0]) && attribute.getSection().equals(layers[1])) {
//                return attribute.getData().get(layers[2]);
//            }
//        }
//        return null;
//    }
//
//    private static JsonNode getEventValue(Event input, String valueLocation) {
//        return input.getData().get(valueLocation);
//    }
//
//    /**
//     * Main access point for Marco Value
//     *
//     * @param valueLocation
//     * @param requestMeta
//     * @param event
//     * @return
//     */
//    private static JsonNode getMarcoValue(String valueLocation, RequestMeta requestMeta, Event event) {
//        switch (valueLocation) {
//            case MARCO_REQUEST_IP:
//                return getObjectMapper().getNodeFactory().textNode(requestMeta.requestIp);
//            case MARCO_USER_AGENT:
//                return getObjectMapper().getNodeFactory().textNode(requestMeta.headers.get("User-Agent"));
//            default:
//                return getMarcoValue(valueLocation, event);
//        }
//    }
//
//    private static JsonNode getMarcoValue(String valueLocation, Event event) {
//        switch (valueLocation) {
//            case MARCO_TIMESTAMP_NOW:
//                return getObjectMapper().getNodeFactory().numberNode(System.currentTimeMillis());
//            case MARCO_TIMESTAMP_EVENT:
//                return getObjectMapper().getNodeFactory().numberNode(event.getCreatedAt().getTime());
//            default:
//                return null;
//        }
//    }
//
//    /**
//     * Main access point for meta Value
//     *
//     * @param valueLocation
//     * @param queueEvent
//     * @return
//     */
//    private static JsonNode getMetaValue(String valueLocation, QueueEvent queueEvent) {
//        switch (valueLocation) {
//            case META_COLLECT_APP:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.session.getCollectApp());
//            case META_SECTION:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.session.getSection());
//            case META_PROFILE_ID:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.profile.getId());
//            case META_APP_SECTION_EVENT:
//                return getObjectMapper().getNodeFactory().textNode(queueEvent.internalEventListenerKey);
//            default:
//                return getMetaValue(valueLocation, queueEvent.meta.companyId, queueEvent.meta.bucketId, queueEvent.event);
//        }
//    }
//
//    private static JsonNode getMetaValue(String valueLocation, String companyId, String bucketId, Event event) {
//        switch (valueLocation) {
//            case META_COMPANY_ID:
//                return getObjectMapper().getNodeFactory().textNode(companyId);
//            case META_BUCKET_ID:
//                return getObjectMapper().getNodeFactory().textNode(bucketId);
//            case META_EVENT_DEFINITION_ID:
//                return getObjectMapper().getNodeFactory().textNode(event.getDefinitionId());
//            default:
//                return null;
//        }
//    }
//
    public InnoTransformer(RulesEntry[] rulesEntries) {
        this.rulesEntries = rulesEntries;
    }

    public InnoTransformer (InnoHelper innoHelper) throws ExecutionException, InterruptedException {
        rulesEntries = innoHelper.getCustom("rules",RulesEntry[].class);
    }

    public Map<String,Object>fromProfile(Profile profile){
        Map <String,Object> result = new HashMap<>();
        final List<Attribute> attributes = profile.getAttributes();
        final Map<String,Object> sessionData = profile.getSessions().get(0).getData();
        final Event event = profile.getSessions().get(0).getEvents().get(0);

//        final QueueEventMeta meta = queueEvent.meta;
        final CrossSystemMessage msg = new CrossSystemMessage();
        //Setting id from canonical id

//        for (FieldSetsEntry fieldSet : rulesEntry.getFieldSets()) {
//            String outGoingFieldName = fieldSet.getSetName();
//            ObjectNode toAdd = getObjectMapper().createObjectNode();
//            for (FieldsEntry field : fieldSet.getFields()) {
//                if (field == null || field.getType() == null) throw new CrossSystemMessageProcessingException(String.format("Field or field type is null for rule %s [%s]", rulesEntry.getName(), rulesEntry.getId()));
//                switch (field.getType()) {
//                    case PROFILE_ATTRIBUTE:
//                        toAdd.set(field.getFieldName(), getUserAttribute(attributes, getValueRef(field)));
//                        break;
//                    case PROFILE_ID:
//                        toAdd.put(field.getFieldName(), profile.getId());
//                        break;
//                    case PROFILE_SIZE:
//                        toAdd.put(field.getFieldName(), queueEvent.meta.profileSize);
//                        break;
//                    case EVENT_VALUE:
//                        toAdd.set(field.getFieldName(), getEventValue(event, getValueRef(field)));
//                        break;
//                    case SESSION_VALUE:
//                        toAdd.set(field.getFieldName(), sessionData.get(getValueRef(field)));
//                        break;
//                    case STATIC_VALUE:
//                        toAdd.set(field.getFieldName(), field.getValueRef());
//                        break;
//                    case MACRO_VALUE:
//                        toAdd.set(field.getFieldName(), getMarcoValue(getValueRef(field), meta.request, event));
//                        break;
//                    case META_VALUE:
//                        //TODO: to be fixed
//                        toAdd.set(field.getFieldName(), getMetaValue(getValueRef(field), queueEvent));
//                        break;
//                    default:
//                        throw new UnsupportedFiledValueTypeException("com.innometrics.model.company.app.FieldsEntry[type]=" + field.getType() + " Not supported");
//                }
//            }
//            if (toAdd.size() > 0) {
//                msg.addField(outGoingFieldName, toAdd);
//            }
//        }
//        return msg;
        return result;
    }

    public Profile toProfile(Map<String,Object> map){
        Profile profile = new Profile();

        return profile;
    }
}

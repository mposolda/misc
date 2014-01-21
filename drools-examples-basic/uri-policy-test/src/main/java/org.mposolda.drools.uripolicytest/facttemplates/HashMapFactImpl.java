package org.mposolda.drools.uripolicytest.facttemplates;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FieldTemplate;
import org.drools.rule.*;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class HashMapFactImpl extends HashMap<String, Object> implements Fact {

    private static AtomicLong staticFactId = new AtomicLong();

    private FactTemplate factTemplate;
    private long factId;

    public HashMapFactImpl() {
        factId = staticFactId.addAndGet(1);
        this.factTemplate = new EmptyFactTemplate();
    }

    public HashMapFactImpl( FactTemplate factTemplate ) {
        factId = staticFactId.addAndGet(1);
        this.factTemplate = factTemplate;
    }

    @Override
    public long getFactId() {
        return factId;
    }

    @Override
    public FactTemplate getFactTemplate() {
        return factTemplate;
    }

    @Override
    public Object getFieldValue(int index) {
        FieldTemplate field = factTemplate.getFieldTemplate(index);
        return get(field.getName());
    }

    @Override
    public Object getFieldValue(String key) {
        return get(key);
    }

    @Override
    public void setFieldValue(int index, Object value) {
        FieldTemplate field = factTemplate.getFieldTemplate(index);
        put( field.getName(), value );
    }

    @Override
    public void setFieldValue(String key, Object value) {
        put(key, value);
    }

    private class EmptyFactTemplate implements FactTemplate {
        @Override
        public org.drools.rule.Package getPackage() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getName() {
            return "hashMapFactImplTemplate";
        }

        @Override
        public int getNumberOfFields() {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public FieldTemplate[] getAllFieldTemplates() {
            return new FieldTemplate[0];  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public FieldTemplate getFieldTemplate(String name) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public FieldTemplate getFieldTemplate(int index) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public int getFieldTemplateIndex(String name) {
            return 0;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Fact createFact(long id) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}

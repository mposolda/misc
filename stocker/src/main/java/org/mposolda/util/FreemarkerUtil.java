package org.mposolda.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import freemarker.cache.URLTemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FreemarkerUtil {

    //private ConcurrentHashMap<String, Template> cache;
    //private final KeycloakSanitizerMethod kcSanitizeMethod = new KeycloakSanitizerMethod();

    public FreemarkerUtil() {
//        if (Config.scope("theme").getBoolean("cacheTemplates", true)) {
//            cache = new ConcurrentHashMap<>();
//        }
    }

    public String processTemplate(Object data, String templateName) {
//        if (data instanceof Map) {
//            ((Map)data).put("kcSanitize", kcSanitizeMethod);
//        }

        try {
            Template template;
//            if (cache != null) {
//                String key = theme.getName() + "/" + templateName;
//                template = cache.get(key);
//                if (template == null) {
//                    template = getTemplate(templateName, theme);
//                    if (cache.putIfAbsent(key, template) != null) {
//                        template = cache.get(key);
//                    }
//                }
//            } else {
                template = getTemplate(templateName);
            //}

            Writer out = new StringWriter();
            template.process(data, out);
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process template " + templateName, e);
        }
    }

    private Template getTemplate(String templateName) throws IOException {
        Configuration cfg = new Configuration();

        // Assume *.ftl files are html.  This lets freemarker know how to
        // sanitize and prevent XSS attacks.
        if (templateName.toLowerCase().endsWith(".ftl")) {
            cfg.setOutputFormat(HTMLOutputFormat.INSTANCE);
        }

        cfg.setTemplateLoader(new ThemeTemplateLoader());
        return cfg.getTemplate(templateName, "UTF-8");
    }

    class ThemeTemplateLoader extends URLTemplateLoader {

//        private Theme theme;
//
//        public ThemeTemplateLoader(Theme theme) {
//            this.theme = theme;
//        }

        @Override
        protected URL getURL(String name) {
            return getClass().getClassLoader().getResource("theme/base/admin/ftemplates/" + name);
        }

    }

}

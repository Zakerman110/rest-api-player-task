package utils;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "AllureAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE,
        printObject = true)
public class AllureAppender extends AbstractAppender {

    protected AllureAppender(String name,
                             Layout<? extends Serializable> layout,
                             Filter filter) {
        super(name, filter, layout, false);
    }

    @PluginFactory
    public static AllureAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new AllureAppender(name, layout, filter);
    }

    @Override
    public void append(LogEvent event) {
        String message = new String(getLayout().toByteArray(event));
        LogCollector.append(message);
    }
}

package net.ishchenko.idea.minibatis.model.sqlmap;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import net.ishchenko.idea.minibatis.converter.PropertyConverter;

/**
 * @author yanglin
 */
public interface PropertyGroup extends DomElement {

    @Attribute("property")
    @Convert(PropertyConverter.class)
    GenericAttributeValue<String> getProperty();
}

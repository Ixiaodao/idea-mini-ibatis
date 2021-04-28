package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Required;

/**
 * @author jiwenbiao
 * @since : 2021/4/28 18:42
 */
public interface IdDomElement extends DomElement {
    @Required
    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();
}

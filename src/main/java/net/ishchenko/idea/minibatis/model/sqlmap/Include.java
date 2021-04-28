package net.ishchenko.idea.minibatis.model.sqlmap;

import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import net.ishchenko.idea.minibatis.converter.SqlConverter;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 02.01.12
 * Time: 20:40
 */
public interface Include extends DomElement {

    @NameValue
    @Attribute("refid")
    @Convert(SqlConverter.class)
    GenericAttributeValue<XmlTag> getRefid();

}

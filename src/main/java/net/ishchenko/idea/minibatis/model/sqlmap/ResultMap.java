package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import net.ishchenko.idea.minibatis.converter.AliasConverter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 22.01.12
 * Time: 0:28
 */
public interface ResultMap extends IdDomElement {


    @Attribute("class")
    @Convert(AliasConverter.class)
    GenericAttributeValue<TypeAlias> getClazz();


    @SubTagList("result")
    List<Result> getResults();

}

package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 23.12.11
 * Time: 23:56
 */
public interface SqlMap extends DomElement {

    @NameValue
    @Attribute("namespace")
    GenericAttributeValue<String> getNamespace();

    @NotNull
    @SubTagsList({"insert", "update", "delete", "select"})
    public List<IdDomElement> getDaoElements();
    
    @SubTagList("sql")
    List<Sql> getSqls();

    @SubTagList("statement")
    List<Statement> getStatements();

    @SubTagList("select")
    List<Select> getSelects();

    @SubTagList("insert")
    List<Insert> getInserts();

    @SubTagList("update")
    List<Update> getUpdates();

    @SubTagList("delete")
    List<Delete> getDeletes();

    @SubTagList("procedure")
    List<Procedure> getProcedures();

    @SubTagList("typeAlias")
    List<TypeAlias> getTypeAliases();

    @SubTagList("resultMap")
    List<ResultMap> getResultMaps();

    @SubTagList("parameterMap")
    List<ParameterMap> getParameterMap();

}

package net.ishchenko.idea.minibatis;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import net.ishchenko.idea.minibatis.model.sqlmap.ResultMap;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMapIdentifiableStatement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 01.01.12
 * Time: 16:03
 */
public class DomFileElementsFinder {

    private final Project project;
    private final DomService domService;
    private final Application application;

    public DomFileElementsFinder(Project project, DomService domService, Application application) {
        this.project = project;
        this.domService = domService;
        this.application = application;
    }

    public void processSqlMapStatements(@NotNull String targetNamespace, @NotNull String targetId, @NotNull Processor<? super SqlMapIdentifiableStatement> processor) {

        nsloop:
        for (DomFileElement<SqlMap> fileElement : findSqlMapFileElements()) {
            SqlMap sqlMap = fileElement.getRootElement();
            String namespace = sqlMap.getNamespace().getRawText();
            if (targetNamespace.equals(namespace) || targetNamespace.length() == 0) {
                for (SqlMapIdentifiableStatement statement : sqlMap.getIdentifiableStatements()) {
                    if (targetId.equals(statement.getId().getRawText())) {
                        if (!processor.process(statement)) {
                            return;
                        }
                        continue nsloop;
                    }
                }
            }
        }

    }

    public void processSqlMapStatementNames(@NotNull Processor<String> processor) {

        for (DomFileElement<SqlMap> fileElement : findSqlMapFileElements()) {
            SqlMap rootElement = fileElement.getRootElement();
            String namespace = rootElement.getNamespace().getRawText();
            for (SqlMapIdentifiableStatement statement : rootElement.getIdentifiableStatements()) {
                String id = statement.getId().getRawText();
                if (id != null && (namespace != null && !processor.process(namespace + "." + id) || namespace == null && !processor.process(id))) {
                    return;
                }
            }
        }

    }

    public void processSqlMaps(@NotNull String targetNamespace, @NotNull Processor<? super SqlMap> processor) {

        for (DomFileElement<SqlMap> fileElement : findSqlMapFileElements()) {
            SqlMap sqlMap = fileElement.getRootElement();
            String namespace = sqlMap.getNamespace().getRawText();
            if (targetNamespace.equals(namespace)) {
                if (!processor.process(sqlMap)) {
                    return;
                }
            }
        }

    }

    public void processSqlMapNamespaceNames(CommonProcessors.CollectProcessor<String> processor) {

        for (DomFileElement<SqlMap> fileElement : findSqlMapFileElements()) {
            SqlMap sqlMap = fileElement.getRootElement();
            if (sqlMap.getNamespace().getRawText() != null && !processor.process(sqlMap.getNamespace().getRawText())) {
                return;
            }
        }
    }

    public void processResultMaps(@NotNull String targetNamespace, @NotNull String targetId, @NotNull Processor<? super ResultMap> processor) {

        nsloop:
        for (DomFileElement<SqlMap> fileElement : findSqlMapFileElements()) {
            SqlMap rootElement = fileElement.getRootElement();
            String namespace = rootElement.getNamespace().getRawText();
            if (targetNamespace.equals(namespace) || targetNamespace.length() == 0 && namespace == null) {
                for (ResultMap resultMap : rootElement.getResultMaps()) {
                    if (targetId.equals(resultMap.getId().getRawText())) {
                        if (!processor.process(resultMap)) {
                            return;
                        }
                        continue nsloop;
                    }
                }
            }

        }

    }

    public void processResultMapNames(@NotNull Processor<String> processor) {

        for (DomFileElement<SqlMap> fileElement : findSqlMapFileElements()) {
            SqlMap rootElement = fileElement.getRootElement();
            String namespace = rootElement.getNamespace().getRawText();
            for (ResultMap resultMap : rootElement.getResultMaps()) {
                String id = resultMap.getId().getRawText();
                if (id != null && (namespace != null && !processor.process(namespace + "." + id) || namespace == null && !processor.process(id))) {
                    return;
                }
            }
        }

    }

    private List<DomFileElement<SqlMap>> findSqlMapFileElements() {
        return domService.getFileElements(SqlMap.class, project, GlobalSearchScope.allScope(project));
    }

}



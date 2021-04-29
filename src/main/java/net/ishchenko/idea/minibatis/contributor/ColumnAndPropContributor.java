package net.ishchenko.idea.minibatis.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomUtil;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.Result;
import net.ishchenko.idea.minibatis.model.sqlmap.ResultMap;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.util.DomUtils;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author jiwenbiao
 * @since : 2021/4/27 18:27
 */
public class ColumnAndPropContributor extends CompletionContributor {
    public ColumnAndPropContributor() {
        System.out.println("123");
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet returnResult) {
        List<ResultMap> resultMaps = getResultMaps(parameters);
        if (CollectionUtils.isEmpty(resultMaps)) {
            return;
        }
        for (ResultMap resultMap : resultMaps) {
            List<Result> results = resultMap.getResults();
            if (CollectionUtils.isEmpty(results)) {
                continue;
            }
            for (Result result : results) {
                String column = result.getColumn().getRawText();
                String property = result.getProperty().getRawText();
                if (StringUtils.isNotEmpty(column)) {
                    returnResult.addElement(LookupElementBuilder.create(column));
                }
                if (StringUtils.isNotEmpty(property)) {
                    returnResult.addElement(LookupElementBuilder.create(property));
                }
            }

        }
        returnResult.stopHere();
    }

    @Nullable
    private List<ResultMap> getResultMaps(@NotNull CompletionParameters parameters) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            return null;
        }
        PsiElement position = parameters.getOriginalPosition();
        if (position == null) {
            return null;
        }
        Editor editor = parameters.getEditor();
        Project project = editor.getProject();
        if (project == null) {
            return null;
        }
        SqlMap mapper = getMapper(position, project);
        if (mapper == null) {
            return null;
        }
        List<ResultMap> resultMaps = mapper.getResultMaps();
        if (CollectionUtils.isEmpty(resultMaps)) {
            return null;
        }
        return resultMaps;
    }

    @Nullable
    private SqlMap getMapper(PsiElement position, Project project) {
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(project);
        PsiFile topLevelFile = injectedLanguageManager.getTopLevelFile(position);
        if (!DomUtils.isIbatisFile(topLevelFile)) {
            return null;
        }
        int offset = injectedLanguageManager.injectedToHost(position, position.getTextOffset());
        IdDomElement idDomElement = SqlMapperUtils.findParentIdDomElement(topLevelFile.findElementAt(offset));
        if (idDomElement == null) {
            return null;
        }
        return DomUtil.getParentOfType(idDomElement, SqlMap.class, true);
    }

}

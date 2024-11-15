package cn.org.expect.maven.intellij.idea.provider;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MavenFindUsagesProvider implements com.intellij.lang.findUsages.FindUsagesProvider {

    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return false;
    }

    public @Nullable @NonNls String getHelpId(@NotNull PsiElement psiElement) {
        return "Find Maven Dependency ID";
    }

    public @Nls @NotNull String getType(@NotNull PsiElement element) {
        return "Find Maven Dependency";
    }

    public @Nls @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        return element.getText();
    }

    public @Nls @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return element.getText();
    }

}

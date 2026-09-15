import type { PluginCreator } from 'postcss';
/**
 * PostCSS plugin to boost specificity for page CSS based on externalClasses usage
 *
 * For mini-program platforms (mp-*):
 *   - If page has no externalClasses usage: no transformation
 *   - If page has dynamic externalClasses: all selectors get page prefix
 *     .a -> page .a
 *   - If page has only static externalClasses: only matching selectors get page prefix
 *     .foo -> page .foo (if "foo" is in staticClasses)
 *     .bar -> .bar (unchanged, if "bar" is not in staticClasses)
 *
 * This ensures page styles have higher specificity than component styles
 * while minimizing unnecessary transformations for performance
 */
declare const externalPlugin: PluginCreator<void>;
export default externalPlugin;

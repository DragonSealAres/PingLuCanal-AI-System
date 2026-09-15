"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const postcss_selector_parser_1 = __importDefault(require("postcss-selector-parser"));
const pages_1 = require("../../json/pages");
const externalClasses_1 = require("../../mp/externalClasses");
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
const externalPlugin = () => {
    return {
        postcssPlugin: 'uni-external',
        prepare() {
            const processedRules = new WeakSet();
            return {
                OnceExit(root) {
                    // Only mini-program platforms need page selector prepend
                    const platform = process.env.UNI_PLATFORM || '';
                    if (!platform.startsWith('mp-')) {
                        return;
                    }
                    // Get file path from postcss source
                    const filePath = root.source?.input?.file;
                    if (!filePath) {
                        return;
                    }
                    // Only process page files
                    if (!(0, pages_1.isUniPageFile)(filePath)) {
                        return;
                    }
                    // Get page's externalClasses info
                    const externalClassesInfo = (0, externalClasses_1.findPageExternalClasses)(filePath);
                    let staticClasses = new Set();
                    let hasDynamic = false;
                    if (process.env.NODE_ENV === 'development') {
                        hasDynamic = true;
                    }
                    else if (externalClassesInfo) {
                        staticClasses = externalClassesInfo.staticClasses;
                        if (externalClassesInfo.hasDynamic ||
                            externalClassesInfo.hasAppAndPageStyle) {
                            hasDynamic = true;
                        }
                    }
                    // If no static classes and no dynamic, skip processing
                    if (staticClasses.size === 0 && !hasDynamic) {
                        return;
                    }
                    root.walkRules((rule) => {
                        processRule(rule, processedRules, staticClasses, hasDynamic);
                    });
                },
            };
        },
    };
};
function processRule(rule, processedRules, staticClasses, hasDynamic) {
    // Skip already processed rules
    if (processedRules.has(rule)) {
        return;
    }
    // Skip keyframes rules
    if (rule.parent &&
        rule.parent.type === 'atrule' &&
        /-?keyframes$/.test(rule.parent.name)) {
        return;
    }
    processedRules.add(rule);
    // Process selector based on externalClasses info
    rule.selector = (0, postcss_selector_parser_1.default)((selectorRoot) => {
        selectorRoot.each((selector) => {
            if (hasDynamic) {
                // Dynamic externalClasses: prepend page to all selectors
                prependPageSelector(selector);
            }
            else {
                // Static only: only prepend page if selector contains any staticClasses
                if (selectorContainsClasses(selector, staticClasses)) {
                    prependPageSelector(selector);
                }
            }
        });
    }).processSync(rule.selector);
}
/**
 * Check if selector contains any of the specified classes
 */
function selectorContainsClasses(selector, classes) {
    let found = false;
    selector.walk((node) => {
        if (node.type === 'class' && classes.has(node.value)) {
            found = true;
            return false; // stop walking
        }
    });
    return found;
}
/**
 * Prepend 'page' selector for mini-program platforms
 * .a -> page .a
 * .b .c -> page .b .c
 */
function prependPageSelector(selector) {
    // Skip if selector already starts with 'page'
    const firstNode = selector.first;
    if (firstNode && firstNode.type === 'tag' && firstNode.value === 'page') {
        return;
    }
    // Insert 'page' tag and a combinator (space) at the beginning
    selector.prepend(postcss_selector_parser_1.default.combinator({ value: ' ' }));
    selector.prepend(postcss_selector_parser_1.default.tag({ value: 'page' }));
}
externalPlugin.postcss = true;
exports.default = externalPlugin;

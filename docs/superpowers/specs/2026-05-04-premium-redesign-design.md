# ClimbUp Premium Frontend Redesign

## Design Decisions

**Palette:** Dark Slate & Electric Blue — deep navy/slate sidebar, light content area, sky-blue accents.  
**Admin layout:** Dark sidebar (`slate-900`) + light content (`slate-50`), Stripe Dashboard style.  
**Member portal:** Dark top nav + gradient hero membership card (consumer/fitness app feel).  
**Auth pages:** Full-page dark (`slate-900`) background, glass-dark card, gradient blue CTA.

## Foundation Changes

- `tailwind.config.js` — extend theme with `primary` (sky), `surface` (slate) aliases; set Inter as `fontFamily.sans`
- `index.html` — add Inter font via Google Fonts `<link>`
- `package.json` — add `lucide-vue-next` for SVG icons (replace all emoji)

## Color Tokens (Tailwind classes used throughout)

| Role | Class |
|---|---|
| Sidebar bg | `bg-slate-900` |
| Active nav | `bg-slate-800`, left `border-sky-500` |
| Page bg | `bg-slate-50` |
| Cards | `bg-white border border-slate-200` |
| Primary CTA | `bg-sky-500 hover:bg-sky-600` / gradient |
| Primary text | `text-slate-900` |
| Secondary text | `text-slate-500` |
| Success / Active | `text-emerald-500` / `bg-emerald-50` |
| Warning / Pending | `text-amber-500` / `bg-amber-50` |
| Danger / Expired | `text-rose-500` / `bg-rose-50` |
| Accent text | `text-sky-500` |

## Component Patterns

**Stat cards:** white card, `border-t-2` colored top border per semantic meaning, uppercase label, bold number.  
**Nav items:** icon + label, active state has `bg-slate-800 border-l-2 border-sky-500 text-sky-400`, inactive `text-slate-400 hover:bg-slate-800/50`.  
**Buttons:** primary = `bg-sky-500 text-white rounded-lg`, secondary = `bg-slate-100 text-slate-700`.  
**Inputs:** `bg-white border border-slate-200 rounded-lg focus:ring-2 focus:ring-sky-500 focus:border-sky-500`.  
**Status badges:** pill with colored bg/text, dot indicator for active memberships.  
**Tables:** `divide-y divide-slate-100`, `hover:bg-slate-50`, header `text-slate-500 text-xs uppercase tracking-wide`.

## Member Hero Card

Gradient `from-slate-900 via-slate-800 to-sky-950`, decorative concentric rings (absolute positioned), active status badge with green dot, member info in 3-column grid.

## Login / Register

Full-page `bg-slate-900` with subtle grid lines and blue radial glow. Card: `bg-slate-800 border border-slate-700 rounded-2xl shadow-2xl`. Focused inputs show `ring-2 ring-sky-500`. Gradient CTA button with `shadow-sky-500/30`.

## Implementation Order (Foundation First)

1. Install `lucide-vue-next`, add Inter to `index.html`, extend `tailwind.config.js`
2. Redesign `AdminLayout.vue` and `MemberLayout.vue`
3. Redesign `LoginView.vue` and `RegisterView.vue`
4. Redesign admin views: Dashboard → Members → MemberDetail → MemberNew → MembershipTypes → MembershipTypeEdit → CheckIn → Discounts → PendingActivations
5. Redesign member views: DashboardView → MembershipsView

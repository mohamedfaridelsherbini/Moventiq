import sharp from "sharp";
import { mkdir, writeFile } from "node:fs/promises";
import { dirname, resolve } from "node:path";

const ROOT = resolve(process.cwd(), "..");
const SVG = (n) => resolve(ROOT, "svg", n);

async function render(src, outRel, size, { flatten = false } = {}) {
  const out = resolve(ROOT, outRel);
  await mkdir(dirname(out), { recursive: true });
  let img = sharp(src, { density: 384 }).resize(size, size, { fit: "contain" });
  if (flatten) img = img.removeAlpha();
  await img.png().toFile(out);
  return `${outRel}  (${size}px)`;
}

const rounded = SVG("moventiq-icon-rounded.svg");
const square = SVG("moventiq-appicon-ios.svg");
const circle = SVG("moventiq-icon-circle.svg");

const log = [];

// Android legacy launcher (pre-API 26 fallback) — rounded square + circle
const androidDensities = [["mdpi", 48], ["hdpi", 72], ["xhdpi", 96], ["xxhdpi", 144], ["xxxhdpi", 192]];
for (const [d, s] of androidDensities) {
  log.push(await render(rounded, `android/mipmap-${d}/ic_launcher.png`, s));
  log.push(await render(circle, `android/mipmap-${d}/ic_launcher_round.png`, s));
}

// Google Play Store listing icon (512, 32-bit)
log.push(await render(square, "store/playstore/ic_launcher-playstore.png", 512));

// iOS app icon set (flattened, no alpha)
const iosSizes = [20, 29, 40, 58, 60, 76, 80, 87, 120, 152, 167, 180, 1024];
for (const s of iosSizes) {
  log.push(await render(square, `ios/AppIcon.appiconset/AppIcon-${s}.png`, s, { flatten: true }));
}

// Apple App Store marketing icon (1024, no alpha)
log.push(await render(square, "store/appstore/AppStore-1024.png", 1024, { flatten: true }));

// iOS asset-catalog Contents.json
const E = (size, scale, idiom, px) => ({ size: `${size}x${size}`, idiom, filename: `AppIcon-${px}.png`, scale: `${scale}x` });
const contents = {
  images: [
    E(20, 2, "iphone", 40), E(20, 3, "iphone", 60),
    E(29, 2, "iphone", 58), E(29, 3, "iphone", 87),
    E(40, 2, "iphone", 80), E(40, 3, "iphone", 120),
    E(60, 2, "iphone", 120), E(60, 3, "iphone", 180),
    E(20, 1, "ipad", 20), E(20, 2, "ipad", 40),
    E(29, 1, "ipad", 29), E(29, 2, "ipad", 58),
    E(40, 1, "ipad", 40), E(40, 2, "ipad", 80),
    E(76, 1, "ipad", 76), E(76, 2, "ipad", 152),
    { size: "83.5x83.5", idiom: "ipad", filename: "AppIcon-167.png", scale: "2x" },
    { size: "1024x1024", idiom: "ios-marketing", filename: "AppIcon-1024.png", scale: "1x" },
  ],
  info: { version: 1, author: "moventiq" },
};
await writeFile(resolve(ROOT, "ios/AppIcon.appiconset/Contents.json"), JSON.stringify(contents, null, 2));
log.push("ios/AppIcon.appiconset/Contents.json");

console.log(log.join("\n"));
console.log(`\nGenerated ${log.length} files.`);

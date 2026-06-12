export interface VehicleTypeOption {
  code: 'cars' | 'motorcycles' | 'trucks';
  description: string;
}

export interface FipeOption {
  code: string;
  name: string;
}

export interface VehicleVariationItem {
  yearId: string;
  modelYear: number;
  brand: string;
  model: string;
  fuel: string;
  fuelAcronym: string;
  codeFipe: string;
  referenceMonth: string;
  price: string;
  priceValue: number;
  changeValue: number | null;
  changeValueFormatted: string | null;
  changePercent: number | null;
  comparedWithYear: number | null;
}

export interface VehicleVariationResponse {
  vehicleType: string;
  brandId: number;
  modelId: number;
  reference: number | null;
  generatedAt: string;
  items: VehicleVariationItem[];
}

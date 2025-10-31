export interface Ad {
    id: string;
    title: string;
    description: string;
    price: number;
    image?: string;
    category: string;
}

interface AdCardProps {
    ad: Ad;
}

import Link from 'next/link';

// ... остальной код тот же ...

export function AdCard({ ad }: AdCardProps) {
    return (
        <Link href={`/ads/${ad.id}`}>
            <div className="border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow cursor-pointer">
                {ad.image && (
                    <img
                        src={ad.image}
                        alt={ad.title}
                        className="w-full h-48 object-cover rounded-md mb-3"
                    />
                )}
                <h3 className="text-lg font-semibold text-gray-900">{ad.title}</h3>
                <p className="text-gray-600 text-sm mt-2 line-clamp-2">{ad.description}</p>
                <div className="flex justify-between items-center mt-4">
                    <span className="text-xl font-bold text-green-600">{ad.price.toLocaleString()} ₸</span>
                    <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded">
            {ad.category}
          </span>
                </div>
            </div>
        </Link>
    );
}